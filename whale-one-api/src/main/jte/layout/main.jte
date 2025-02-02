@param gg.jte.Content content

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Whale One</title>
    <script src="https://cdn.tailwindcss.com"></script>
</head>
<body class="bg-gray-100">
<nav class="bg-white shadow">
    <div class="container mx-auto px-4">
        <div class="flex items-center justify-between py-4">
            <a href="/" class="text-xl font-bold">Whale One</a>
            <div>
                <a href="/deployments" class="text-blue-500 hover:underline ml-4">Deployments</a>
                <a href="/equipment" class="text-blue-500 hover:underline ml-4">Equipment</a>
            </div>
        </div>
    </div>
</nav>

<main class="container mx-auto mt-4 p-4">
    ${content}
</main>

<footer class="bg-white mt-8 py-4">
    <!-- Footer content -->
</footer>
</body>
</html>