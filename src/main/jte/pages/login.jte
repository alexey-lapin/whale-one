@param gg.jte.Content content

<!doctype html>
<html lang="en">
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <meta name="color-scheme" content="light dark">
    <title>Whale One</title>
    <link href="https://cdn.jsdelivr.net/npm/bulma@1.0.2/css/bulma.min.css" rel="stylesheet">
    <link href="https://cdn.jsdelivr.net/npm/primeicons@7.0.0/primeicons.min.css " rel="stylesheet">
    <link href="/styles.css" rel="stylesheet">
</head>
<body>
<section class="hero is-fullheight">
    <div class="hero-body">
        <div class="container">
            <div class="columns is-centered">
                <div class="column is-4">
                    <form action="/login" method="post" class="box">
                        <h1 class="title is-4 has-text-centered is-size-1">🐳</h1>
                        <div class="field">
                            <div class="control has-icons-left">
                                <input class="input" type="text" name="username" placeholder="Username"
                                       required>
                                <span class="icon is-small is-left">
                                    <i class="pi pi-user"></i>
                                </span>
                            </div>
                        </div>
                        <div class="field">
                            <div class="control has-icons-left">
                                <input class="input" type="password" name="password" placeholder="Password"
                                       required>
                                <span class="icon is-small is-left">
                                    <i class="pi pi-lock"></i>
                                </span>
                            </div>
                        </div>
                        <div class="field">
                            <button type="submit" class="button is-primary is-fullwidth">Login</button>
                        </div>
                    </form>
                </div>
            </div>
        </div>
    </div>
</section>
</body>
</html>