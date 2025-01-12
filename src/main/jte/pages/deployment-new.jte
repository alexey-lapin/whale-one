@import java.util.List
@import org.springframework.boot.info.BuildProperties
@import org.springframework.validation.BindingResult

@param BindingResult bindingResult = null
@param BuildProperties buildProperties

@template.layout.bulma(buildProperties = buildProperties, content = @`
    <form action="/deployments/new" method="post">
        <%--        <div class="field">--%>
        <%--            <label class="label">Type</label>--%>
        <%--            <div class="control">--%>
        <%--                <div class="select">--%>
        <%--                    <select name="type">--%>
        <%--                        <option></option>--%>
        <%--                        @for(var user : equipmentTypes)--%>
        <%--                            <option value="${user.deploymentId()}">${user.name()}</option>--%>
        <%--                        @endfor--%>
        <%--                    </select>--%>
        <%--                </div>--%>
        <%--            </div>--%>
        <%--        </div>--%>
        <div class="field is-hidden">
            <div class="control">
                <input class="input" type="hidden" name="id" value="0">
            </div>
        </div>
        <div class="field">
            <label class="label">Name</label>
            <div class="control">
                <input class="input ${bindingResult != null && bindingResult.hasFieldErrors("name") ? "is-danger" : ""}"
                       type="text"
                       name="name"
                       placeholder="Name">
            </div>
            @if(bindingResult != null && bindingResult.hasFieldErrors("name"))
                <p class="help is-danger">${bindingResult.getFieldError("name").getDefaultMessage()}</p>
            @endif
        </div>
        <div class="field">
            <label class="label">Description</label>
            <div class="control">
                <input class="input" type="text" name="description" placeholder="Description">
            </div>
        </div>
        <div class="field is-grouped">
            <div class="control">
                <button class="button is-link">Save</button>
            </div>
            <a href="/deployments" class="button is-light">Back</a>
        </div>
    </form>
`)