#!/usr/bin/env python3
import subprocess
import sys
import pathlib
import re

# -----------------------------
# Paths
# -----------------------------
ROOT = pathlib.Path(__file__).resolve().parents[3]
SPEC_PATH = ROOT / "contract" / "events.yaml"
OUT_DIR = ROOT / "backend" / "extraction" / "contracts" / "models"
PACKAGE_NAME = ""

# -----------------------------
# Utils
# -----------------------------
def die(msg: str):
    print(f"❌ {msg}", file=sys.stderr)
    sys.exit(1)


def assert_spec_is_correct():
    if not SPEC_PATH.exists():
        die(f"AsyncAPI spec not found: {SPEC_PATH}")

    text = SPEC_PATH.read_text()

    # Guard: ensure camelCase fields exist
    required_fields = ["requestId", "itemId", "fileLocation"]
    for f in required_fields:
        if f not in text:
            die(f"Spec does not contain expected field '{f}' (camelCase). Wrong spec?")

    print("✅ AsyncAPI spec looks correct (camelCase fields found)")


def run_asyncapi_generator():
    OUT_DIR.mkdir(parents=True, exist_ok=True)

    cmd = [
        "npx",
        "@asyncapi/cli",
        "generate",
        "models",
        "python",
        str(SPEC_PATH),
        "-o",
        str(OUT_DIR),
        "--packageName",
        PACKAGE_NAME,
    ]

    print("▶ Running AsyncAPI generator:")
    print(" ".join(cmd))

    subprocess.run(cmd, check=True)


def patch_snake_case_to_camel_case():
    """
    AsyncAPI python models often access dict like input['request_id'].
    Patch generated models to also support camelCase keys.
    """

    replacements = {
        "request_id": "requestId",
        "item_id": "itemId",
        "file_location": "fileLocation",
    }

    py_files = list(OUT_DIR.rglob("*.py"))
    if not py_files:
        die("No python files generated – generator probably failed")

    print(f"▶ Post-processing {len(py_files)} python files")

    for file in py_files:
        text = file.read_text()
        original = text

        for snake, camel in replacements.items():
            # input['request_id'] -> input.get('request_id') or input.get('requestId')
            text = re.sub(
                rf"input\[['\"]{snake}['\"]\]",
                f"(input.get('{snake}') or input.get('{camel}'))",
                text,
            )

        if text != original:
            file.write_text(text)
            print(f"  🩹 patched {file.relative_to(OUT_DIR)}")


# -----------------------------
# Main
# -----------------------------
def run():
    print("ROOT      =", ROOT)
    print("SPEC_PATH =", SPEC_PATH)
    print("OUT_DIR   =", OUT_DIR)

    assert_spec_is_correct()
    run_asyncapi_generator()
    patch_snake_case_to_camel_case()

    print(f"✅ AsyncAPI Python models generated & patched in {OUT_DIR}")


if __name__ == "__main__":
    try:
        run()
    except subprocess.CalledProcessError as e:
        die("AsyncAPI model generation failed")
