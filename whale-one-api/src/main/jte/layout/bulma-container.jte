@import org.springframework.boot.info.BuildProperties

@param BuildProperties buildProperties
@param gg.jte.Content additionalHeadContent = null
@param gg.jte.Content content

@template.layout.bulma(
buildProperties = buildProperties,
additionalHeadContent = additionalHeadContent,
content = @`
    <main class="section pt-0">
        <div class="container">
            ${content}
        </div>
    </main>
`)
