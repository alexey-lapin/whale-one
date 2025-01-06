@param gg.jte.Content content

<!doctype html>
<html lang="en">
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <meta name="color-scheme" content="light dark">
<%--    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/@picocss/pico@2/css/pico.min.css">--%>
    <link rel="stylesheet" href="https://unpkg.com/primeflex@latest/primeflex.css">
    <title>Hello world!</title>
    <style>
        :root {
            --pico-font-family-sans-serif: Inter, system-ui, "Segoe UI", Roboto, Oxygen, Ubuntu, Cantarell, Helvetica, Arial, "Helvetica Neue", sans-serif, var(--pico-font-family-emoji);
            --pico-font-size: 87.5%;
            /* Original: 100% */
            --pico-line-height: 1.25;
            /* Original: 1.5 */
            --pico-form-element-spacing-vertical: 0.5rem;
            /* Original: 1rem */
            --pico-form-element-spacing-horizontal: 1.0rem;
            /* Original: 1.25rem */
            --pico-border-radius: 0.375rem;
            /* Original: 0.25rem */
        }

        @media (min-width: 576px) {
            :root {
                --pico-font-size: 87.5%;
                /* Original: 106.25% */
            }
        }

        @media (min-width: 768px) {
            :root {
                --pico-font-size: 87.5%;
                /* Original: 112.5% */
            }
        }

        @media (min-width: 1024px) {
            :root {
                --pico-font-size: 87.5%;
                /* Original: 118.75% */
            }
        }

        @media (min-width: 1280px) {
            :root {
                --pico-font-size: 87.5%;
                /* Original: 125% */
            }
        }

        @media (min-width: 1536px) {
            :root {
                --pico-font-size: 87.5%;
                /* Original: 131.25% */
            }
        }

        h1,
        h2,
        h3,
        h4,
        h5,
        h6 {
            --pico-font-weight: 600;
            /* Original: 700 */
        }

        article {
            border: 1px solid var(--pico-muted-border-color);
            /* Original doesn't have a border */
            border-radius: calc(var(--pico-border-radius) * 2);
            /* Original: var(--pico-border-radius) */
        }

        article>footer {
            border-radius: calc(var(--pico-border-radius) * 2);
            /* Original: var(--pico-border-radius) */
        }
    </style>
</head>
<body>
<header class="container">
<%--    <hgroup>--%>
<%--        <h1>Pico</h1>--%>
<%--        <p>A pure HTML example, without dependencies.</p>--%>
<%--    </hgroup>--%>
    <nav>
        <div class="flex items-center justify-between py-4">
            <a href="/" class="text-xl font-bold">Whale One</a>
            <div>
                <a href="/deployments" class="text-blue-500 hover:underline ml-4">Deployments</a>
                <a href="/equipment" class="text-blue-500 hover:underline ml-4">Equipment</a>
            </div>
        </div>
        <ul>
            <li>
                <details class="dropdown">
                    <summary role="button" class="secondary">Theme</summary>
                    <ul>
                        <li><a href="#" data-theme-switcher="auto">Auto</a></li>
                        <li><a href="#" data-theme-switcher="light">Light</a></li>
                        <li><a href="#" data-theme-switcher="dark">Dark</a></li>
                    </ul>
                </details>
            </li>
        </ul>
    </nav>
</header>
<main class="container">
    ${content}
</main>
</body>
</html>