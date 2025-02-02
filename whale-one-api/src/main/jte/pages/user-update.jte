@import com.github.alexeylapin.whaleone.domain.model.User
@import org.springframework.boot.info.BuildProperties
@import org.springframework.validation.BindingResult

@param User user
@param BindingResult bindingResult = null
@param BuildProperties buildProperties

@template.layout.bulma-container(buildProperties = buildProperties, content = @`
    <form action="/users/${user.id()}" method="post">
        <div class="field is-hidden">
            <div class="control">
                <input class="input"
                       type="text"
                       name="id"
                       value="${user.id()}"
                >
            </div>
        </div>
        <div class="field is-hidden">
            <div class="control">
                <input class="input"
                       type="text"
                       name="version"
                       value="${user.version()}"
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
                       value="${user.username()}"
                       required
                >
            </div>
            @if(bindingResult != null && bindingResult.hasFieldErrors("name"))
                <p class="help is-danger">${bindingResult.getFieldError("name").getDefaultMessage()}</p>
            @endif
        </div>
        <div class="field">
            <label class="label">New Password</label>
            <div class="control">
                <input class="input"
                       type="password"
                       name="password"
                       placeholder="Password"
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
                >
            </div>
        </div>
        <div class="field">
            <div class="control">
                <label class="checkbox">
                    <input type="checkbox" name="enabled" checked="${user.enabled()}">
                    <input type="hidden" name="enabled" value="false">
                    Is Enabled
                </label>
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