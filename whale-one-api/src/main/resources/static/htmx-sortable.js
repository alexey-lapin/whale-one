htmx.onLoad(function (content) {
    var sortables = content.querySelectorAll(".sortable");
    console.log(sortables);
    for (var i = 0; i < sortables.length; i++) {
        var sortable = sortables[i];
        var sortableInstance = new Sortable(sortable, {
            animation: 150,
            ghostClass: 'sortable-grabbing',
            handle: '.sortable-handle',

            // Make the `.htmx-indicator` unsortable
            filter: ".htmx-indicator",
            onMove: function (evt) {
                return evt.related.className.indexOf('htmx-indicator') === -1;
            },

            // Disable sorting on the `end` event
            onEnd: function (evt) {
                this.option("disabled", true);
            }
        });

        // Re-enable sorting on the `htmx:afterSwap` event
        sortable.addEventListener("htmx:afterSwap", function () {
            sortableInstance.option("disabled", false);
        });
    }
})

document.addEventListener('DOMContentLoaded', () => {

    // Get all "navbar-burger" elements
    const $navbarBurgers = Array.prototype.slice.call(document.querySelectorAll('.navbar-burger'), 0);

    // Add a click event on each of them
    $navbarBurgers.forEach(el => {
        el.addEventListener('click', () => {

            // Get the target from the "data-target" attribute
            const target = el.dataset.target;
            const $target = document.getElementById(target);

            // Toggle the "is-active" class on both the "navbar-burger" and the "navbar-menu"
            el.classList.toggle('is-active');
            $target.classList.toggle('is-active');

        });
    });

});

document.addEventListener('DOMContentLoaded', () => {
    const themeToggleButton = document.getElementById('theme-toggle-btn');
    const themeIcon = document.getElementById('theme-icon');

    // Apply theme based on user preference or system default
    function applyTheme(theme) {
        if (theme === 'light') {
            applyHtmlTheme('light');
            lightButton(false);
        } else if (theme === 'dark') {
            applyHtmlTheme('dark');
            darkButton(false);
        } else {
            // System theme (default)
            if (window.matchMedia('(prefers-color-scheme: dark)').matches) {
                applyHtmlTheme('dark');
                darkButton(true)
            } else {
                applyHtmlTheme('light');
                lightButton(true)
            }
        }
    }

    function lightButton(isGhost) {
        themeIcon.className = 'pi pi-sun';
        themeToggleButton.classList.remove('is-dark');
        themeToggleButton.classList.remove('has-text-link');
        themeToggleButton.classList.add('is-light');
        themeToggleButton.classList.add('has-text-warning');
        if (isGhost) {
            themeToggleButton.classList.add('is-ghost');
        }
    }

    function darkButton(isGhost) {
        themeIcon.className = 'pi pi-moon';
        themeToggleButton.classList.remove('is-light');
        themeToggleButton.classList.remove('has-text-warning');
        themeToggleButton.classList.add('is-dark');
        themeToggleButton.classList.add('has-text-link');
        if (isGhost) {
            themeToggleButton.classList.add('is-ghost');
        }
    }

    // Toggle between light and dark themes
    function toggleTheme() {
        const currentTheme = loadTheme();
        let newTheme;

        if (currentTheme === 'dark') {
            newTheme = 'light';
        } else {
            newTheme = 'dark';
        }

        saveTheme(newTheme);
        applyTheme(newTheme);
    }

    // Initialize theme
    const theme = loadTheme();
    applyTheme(theme);

    // Listen for system theme changes if "system" is selected
    if (theme === 'system') {
        window.matchMedia('(prefers-color-scheme: dark)').addEventListener('change', () => {
            applyTheme('system');
        });
    }

    // Add click event listener to toggle button
    if (themeToggleButton) {
        themeToggleButton.addEventListener('click', toggleTheme);
    }

    if (themeToggleButton) {
        themeToggleButton.addEventListener('dblclick', function () {
            saveTheme('system');
            applyTheme('system');
        });
    }
});

// Load saved theme or return system by default
function loadTheme() {
    const savedTheme = localStorage.getItem('theme');
    return savedTheme || 'system';
}

// Save theme to localStorage (if not system)
function saveTheme(theme) {
    if (theme === 'system') {
        localStorage.removeItem('theme'); // Clear saved theme
    } else {
        localStorage.setItem('theme', theme);
    }
}

function applyHtmlTheme(theme) {
    const htmlElement = document.documentElement;
    htmlElement.classList.remove('theme-light', 'theme-dark');
    if (theme !== 'system') {
        htmlElement.classList.add(`theme-${theme}`);
    }
}

applyHtmlTheme(loadTheme());