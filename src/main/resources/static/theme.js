(function () {
    var saved = null;
    try { saved = localStorage.getItem("theme"); } catch (e) {}

    var prefersDark = window.matchMedia &&
        window.matchMedia("(prefers-color-scheme: dark)").matches;
    var theme = saved || (prefersDark ? "dark" : "light");

    // Apply immediately so the page doesn't flash white
    document.documentElement.setAttribute("data-theme", theme);

    document.addEventListener("DOMContentLoaded", function () {
        var toggle = document.getElementById("themeToggle");
        if (!toggle) return;

        toggle.checked = theme === "dark";

        toggle.addEventListener("change", function () {
            var t = toggle.checked ? "dark" : "light";
            document.documentElement.setAttribute("data-theme", t);
            try { localStorage.setItem("theme", t); } catch (e) {}

            var meta = document.querySelector('meta[name="theme-color"]');
            if (meta) meta.setAttribute("content", t === "dark" ? "#0f172a" : "#3b82f6");
        });
    });
})();
