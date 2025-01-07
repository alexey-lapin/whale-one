@import org.springframework.boot.info.BuildProperties

@param BuildProperties buildProperties
@param gg.jte.Content content

<!doctype html>
<html lang="en">
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <meta name="color-scheme" content="light dark">
    <title>Whale One</title>
    <link href="https://cdn.jsdelivr.net/npm/bulma@1.0.2/css/bulma.min.css" rel="stylesheet">
    <link href="https://cdn.rawgit.com/octoshrimpy/bulma-o-steps/master/bulma-steps.css" rel="stylesheet">
    <link href="https://cdn.jsdelivr.net/npm/primeicons@7.0.0/primeicons.min.css " rel="stylesheet">
    <link href="/styles.css" rel="stylesheet">
    <script src="https://unpkg.com/htmx.org@2.0.4"></script>
    <script src="https://unpkg.com/sortablejs@1.15.6/Sortable.min.js"></script>
    <script src="/htmx-sortable.js"></script>
</head>
<body>
<nav class="navbar my-4" role="navigation" aria-label="main navigation">
    <div class="container">
        <div class="navbar-brand">
            <a class="navbar-item is-size-3" href="/">
                <strong>🐳 Whale One</strong>
            </a>
            <a role="button" class="navbar-burger" aria-label="menu" aria-expanded="false" data-target="navbarBasicExample">
                <span aria-hidden="true"></span>
                <span aria-hidden="true"></span>
                <span aria-hidden="true"></span>
                <span aria-hidden="true"></span>
            </a>
        </div>
        <div id="navbarBasicExample" class="navbar-menu">
            <div class="navbar-start">
                <a class="navbar-item" href="/deployments">
                    Deployments
                </a>
                <a class="navbar-item" href="/equipment">
                    Equipment
                </a>
            </div>
            <div class="navbar-end">
                <div class="navbar-item">
                    <div class="buttons">
                        <a href="/logout" class="button is-light">
                            <strong>Logout</strong>
                        </a>
                    </div>
                </div>
            </div>
        </div>
    </div>
</nav>
<main class="section pt-0">
    <div class="container">
        ${content}
    </div>
</main>
<footer class="py-1 is-hidden-mobile">
    <div class="content has-text-centered">
        <p class="has-text-grey-light">v${buildProperties.getVersion()}</p>
    </div>
</footer>
</body>
</html>