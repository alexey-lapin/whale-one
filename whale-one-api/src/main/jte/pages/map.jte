@import java.util.List

@param List<com.github.alexeylapin.whaleone.domain.model.DeploymentMarker> markers
@param org.springframework.boot.info.BuildProperties buildProperties

@template.layout.bulma(buildProperties = buildProperties,
additionalHeadContent = @`
    <link rel="stylesheet" href="https://unpkg.com/leaflet/dist/leaflet.css" />
    <script src="https://unpkg.com/leaflet/dist/leaflet.js"></script>
`,
content = @`
    <div id="map" class="is-flex-grow-1"></div>
    <script>
        var map = L.map('map').setView([-31.9816, 115.8275], 5);

        L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
            maxZoom: 19,
        }).addTo(map);

        @for(var marker : markers)
            L.marker([$unsafe{String.valueOf(marker.latitude())}, $unsafe{String.valueOf(marker.longitude())}]).addTo(map)
                .bindPopup("<a href='/deployments/${marker.id()}'># ${marker.id()}</a>");
        @endfor
    </script>
`)