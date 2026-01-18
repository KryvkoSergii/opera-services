import { spawn } from "node:child_process";
import path from "node:path";
import process from "node:process";
import fs from "node:fs/promises";

/**
 * Generates TypeScript OpenAPI types into src/services/*.ts
 * + Generates flat model aliases into src/services/*.models.ts
 */

async function findDirUpwards(startDir, dirName) {
    let current = startDir;

    while (current) {
        const candidate = path.join(current, dirName);

        try {
            const stat = await fs.stat(candidate);
            if (stat.isDirectory()) return candidate;
        } catch {}

        const parent = path.dirname(current);
        current = parent !== current ? parent : null;
    }

    throw new Error(`Directory '${dirName}' not found starting from ${startDir}`);
}

function run(cmd, args) {
    return new Promise((resolve, reject) => {
        const child = spawn(cmd, args, {
            stdio: "inherit",
            shell: process.platform === "win32",
        });

        child.on("error", reject);
        child.on("exit", (code) => {
            if (code === 0) resolve();
            else reject(new Error(`${cmd} exited with code ${code}`));
        });
    });
}

async function generateTypes(input, output) {
    console.log(`\nGenerating TypeScript OpenAPI types...`);
    console.log(`  Spec:   ${input}`);
    console.log(`  Output: ${output}`);

    await fs.mkdir(path.dirname(output), { recursive: true });

    const args = [
        "-y",
        "openapi-typescript@latest",
        input,
        "-o",
        output,
        "--alphabetize",
        "--immutable-types",
    ];

    await run("npx", args);
    console.log(`✅ OpenAPI types generated: ${output}`);
}

/**
 * Generate "flat" model aliases file:
 *   user.models.ts with exports like Gender, UserRegisterRequest, ...
 *
 * This version is "spec-aware" only via a small per-service map.
 */
async function generateModels(serviceName, typesOutFile) {
    const base = path.basename(typesOutFile, ".ts"); // auth/core/user
    const modelsOutFile = typesOutFile.replace(/\.ts$/, ".models.ts");

    const contentByService = {
        auth: `/**
 * AUTO-GENERATED FLAT MODELS (aliases)
 * Do not edit manually.
 */
import type { components, paths } from "./${base}";

export type UserLoginRequest =
  paths["/v1/auth/login"]["post"]["requestBody"]["content"]["application/json"];

export type TokenResponse =
  paths["/v1/auth/login"]["post"]["responses"][200]["content"]["application/json"];

export type ErrorResponse = components["schemas"]["ErrorResponse"];
`,
        user: `/**
 * AUTO-GENERATED FLAT MODELS (aliases)
 * Do not edit manually.
 */
import type { components, paths } from "./${base}";

export type Gender = components["schemas"]["Gender"];

export type UserRegisterRequest =
  paths["/v1/users/register"]["post"]["requestBody"]["content"]["application/json"];

export type UserRegisterResponse =
  paths["/v1/users/register"]["post"]["responses"][201]["content"]["application/json"];

export type MeUserDetailsResponse =
  paths["/v1/users/me"]["get"]["responses"][200]["content"]["application/json"];

export type ErrorResponse = components["schemas"]["ErrorResponse"];
`,
        core: `/**
 * AUTO-GENERATED FLAT MODELS (aliases)
 * Do not edit manually.
 */
import type { components, paths } from "./${base}";

export type SourceType = components["schemas"]["SourceType"];
export type RequestStatus = components["schemas"]["RequestStatus"];
export type ProbabilityStatus = components["schemas"]["ProbabilityStatus"];

export type PaginatedRequestHistory =
  paths["/v1/analyses"]["get"]["responses"][200]["content"]["application/json"];

export type AnalysisCreateResponse =
  paths["/v1/analyses"]["post"]["responses"][201]["content"]["application/json"];

export type ErrorResponse = components["schemas"]["ErrorResponse"];
`,
    };

    const content = contentByService[serviceName];
    if (!content) throw new Error(`No model template for service '${serviceName}'`);

    await fs.writeFile(modelsOutFile, content, "utf8");
    console.log(`✅ Flat models generated: ${modelsOutFile}`);
}

async function generateService(serviceName, specFile) {
    const out = path.resolve(`src/services/${serviceName}.ts`);
    await generateTypes(specFile, out);
    await generateModels(serviceName, out);
}

const OPENAPI_SPEC_ROOT = await findDirUpwards(process.cwd(), "contract");

// sequential (stable)
await generateService("auth", path.resolve(OPENAPI_SPEC_ROOT, "auth-api.yaml"));
await generateService("core", path.resolve(OPENAPI_SPEC_ROOT, "core-api.yaml"));
await generateService("user", path.resolve(OPENAPI_SPEC_ROOT, "user-api.yaml"));
