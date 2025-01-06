@import com.github.alexeylapin.whaleone.domain.model.Equipment

@template.layout.bulma(content = @`
    <form action="/equipment/types/new" method="post">
        <div class="field" style="display: none">
            <label class="label">Id</label>
            <div class="control">
                <input class="input"
                       type="text"
                       name="id"
                       value="0"
                >
            </div>
        </div>
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