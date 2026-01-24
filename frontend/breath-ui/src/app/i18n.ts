import i18n from "i18next";
import {initReactI18next} from "react-i18next";

const resources = {
    en: {
        translation: {
            app: {title: "Breath Analysis", beta: "beta"},
            nav: {records: "Records", history: "History", logout: "Logout"},
            common: {ok: "OK", optional: "optional"},
            cookie: {
                text: "This website uses cookies / local storage to keep you signed in and improve your experience.",
            },

            login: {
                title: "Login",
                subtitle: "Sign in to record and review analyses",
                email: "Email",
                password: "Password",
                submit: "Sign in",
                create: "Create account",
                error: "Invalid email or password",
                retry: "Failed attempt. Please try again",
                signing: "Signing in..."
            },

            register: {
                title: "Register",
                subtitle: "Create a new account",
                login: "Login",
                password: "Password",
                passwordRepeat: "Repeat password",
                gender: "Gender",
                male: "Male",
                female: "Female",
                genderOptionalHint: "This field is optional.",
                submit: "Create account",
                back: "Back to login",
                errors: {
                    loginRequired: "Login is required",
                    passwordRequired: "Password is required",
                    passwordMin: "Min 6 chars",
                    repeatRequired: "Please repeat password",
                    mismatch: "Passwords do not match",
                },
            },

            records: {
                title: "Records",
                subtitle: "Record from microphone or upload a file, then send for analysis",
                tip: "Tip: On mobile, grant microphone permission when prompted.",
                usageWarning: "Do not use the system for diagnosis or clinical decision making.",
                source: "Source",
                sourceMic: "Microphone",
                sourceStetho: "Stethoscope",
                record: "Record",
                stop: "Stop",
                uploadTitle: "Upload file",
                chooseFile: "Choose file",
                send: "Send",
                noAudio: "No audio selected yet.",
                uploading: "Uploading...",
                uploaded: "Uploaded...",
                processing: "Processing...",
                failed: "Failed.",
                completed: "Completed.",
                needAudio: "Please record audio or select a file before sending.",
            },

            history: {
                title: "History",
                subtitle: "Requests history and results",
                sentAt: "Sent at",
                status: "Status",
                diagnosis: "Examination results",
                recommendation: "Recommendation",
                emptyTitle: "No requests yet",
                emptyText: "Record audio or upload a file to create your first analysis request.",
                emptyCta: "Go to Records",
                source: "Source",
                high_risk: "Seek medical attention immediately",
                medium_risk: "Consult a doctor",
                low_risk: "Monitor symptoms at home",
            },
        },
    },
    uk: {
        translation: {
            app: {title: "Діагностика дихання", beta: "beta"},
            nav: {records: "Записи", history: "Історія", logout: "Вийти"},
            common: {ok: "OK", optional: "опційно"},
            cookie: {
                text: "Сайт використовує cookies / local storage для збереження сесії та покращення досвіду.",
            },

            login: {
                title: "Вхід",
                subtitle: "Увійдіть, щоб записувати та переглядати аналізи",
                email: "Електронна пошта",
                password: "Пароль",
                submit: "Увійти",
                create: "Створити акаунт",
                error: "Невірний емейл або пароль",
                retry: "Невдала спроба. Спробуйте ще раз",
                signing: "Вхід..."
            },

            register: {
                title: "Реєстрація",
                subtitle: "Створіть новий акаунт",
                login: "Логін",
                password: "Пароль",
                passwordRepeat: "Повторіть пароль",
                gender: "Стать",
                male: "Чоловік",
                female: "Жінка",
                genderOptionalHint: "Поле опційне.",
                submit: "Створити акаунт",
                back: "Назад до входу",
                errors: {
                    loginRequired: "Логін обовʼязковий",
                    passwordRequired: "Пароль обовʼязковий",
                    passwordMin: "Мінімум 6 символів",
                    repeatRequired: "Повторіть пароль",
                    mismatch: "Паролі не співпадають",
                },
            },

            records: {
                title: "Записи",
                subtitle: "Запишіть з мікрофону або завантажте файл, потім відправте на аналіз",
                tip: "Порада: на мобільному підтвердіть доступ до мікрофона.",
                usageWarning: "Не використовуйте систему для постановки діагнозу або прийняття клінічних рішень.",
                source: "Джерело",
                sourceMic: "Мікрофон",
                sourceStetho: "Стетоскоп",
                record: "Записати",
                stop: "Зупинити",
                uploadTitle: "Завантажити файл",
                chooseFile: "Обрати файл",
                send: "Відправити",
                noAudio: "Аудіо ще не обрано.",
                uploading: "Завантаження...",
                uploaded: "Завантажено...",
                processing: "Обробка...",
                failed: "Помилка.",
                completed: "Готово.",
                needAudio: "Спочатку запишіть аудіо або оберіть файл.",
            },

            history: {
                title: "Історія",
                subtitle: "Історія запитів і результати",
                sentAt: "Відправлено",
                status: "Статус",
                diagnosis: "Результати дослідження",
                recommendation: "Рекомендація",
                emptyTitle: "Поки що немає запитів",
                emptyText: "Запишіть аудіо або завантажте файл, щоб створити перший запит.",
                emptyCta: "Перейти до Записів",
                source: "Джерело",
                high_risk: "Зверністься до лікаря негайно",
                medium_risk: "Проконсультуйтесь з лікарем",
                low_risk: "Слідкуйте за симптомами вдома",
            },
        },
    },
};

i18n.use(initReactI18next).init({
    resources,
    lng: "uk",
    fallbackLng: "en",
    interpolation: {escapeValue: false},
});

export default i18n;
