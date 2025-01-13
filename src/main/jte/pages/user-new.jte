@import org.springframework.boot.info.BuildProperties
@import org.springframework.validation.BindingResult

@param BindingResult bindingResult = null
@param BuildProperties buildProperties

@template.layout.bulma-container(buildProperties = buildProperties, content = @`
    <form action="/users/new" method="post">
        <div class="field is-hidden">
            <div class="control">
                <input class="input"
                       type="text"
                       name="id"
                       value="0"
                >
            </div>
        </div>
        <div class="field is-hidden">
            <div class="control">
                <input class="input"
                       type="text"
                       name="version"
                       value="0"
                >
            </div>
        </div>
        <div class="field">
            <label class="label">Username</label>
            <div class="control">
                <input class="input ${bindingResult != null && bindingResult.hasFieldErrors("name") ? "is-danger" : ""}"
                       type="text"
                       name="username"
                       placeholder="Name"
                       required
                >
            </div>
            @if(bindingResult != null && bindingResult.hasFieldErrors("name"))
                <p class="help is-danger">${bindingResult.getFieldError("name").getDefaultMessage()}</p>
            @endif
        </div>
        <div class="field">
            <label class="label">Password</label>
            <div class="control">
                <input class="input"
                       type="password"
                       name="password"
                       placeholder="Password"
                       required
                >
            </div>
        </div>
        <div class="field">
            <label class="label">Confirm Password</label>
            <div class="control">
                <input class="input"
                       type="password"
                       name="confirmPassword"
                       placeholder="Password"
                       required
                >
            </div>
        </div>
        <div class="field is-hidden">
            <label class="label">Is Enabled</label>
            <div class="control">
                <input class="input"
                       type="text"
                       name="enabled"
                       value="true"
                >
            </div>
        </div>
        <div class="field is-grouped">
            <div class="control">
                <button class="button is-link">Save</button>
            </div>
            <a href="/users" class="button is-light">Back</a>
        </div>
    </form>
`)