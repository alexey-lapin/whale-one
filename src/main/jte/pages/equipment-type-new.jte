@import org.springframework.boot.info.BuildProperties

@param BuildProperties buildProperties

@template.layout.bulma-container(buildProperties = buildProperties, content = @`
    <form action="/equipment/types/new" method="post">
        <input type="hidden" name="id" value="0">
        <input type="hidden" name="version" value="0">
        <input type="hidden" name="createdById" value="1">
        <div class="field">
            <label class="label">Name</label>
            <div class="control">
                <input class="input"
                       type="text"
                       name="name"
                       placeholder="Name"
                       required
                >
            </div>
        </div>
        <div class="field is-grouped">
            <div class="control">
                <button class="button is-link">Save</button>
            </div>
            <a href="/equipment/types" class="button is-light">Back</a>
        </div>
    </form>
`)