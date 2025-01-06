@import java.util.List
@import org.springframework.validation.BindingResult

@param BindingResult bindingResult = null


@template.layout.bulma(content = @`
    <form action="/deployments/new" method="post">
        <%--        <div class="field">--%>
        <%--            <label class="label">Type</label>--%>
        <%--            <div class="control">--%>
        <%--                <div class="select">--%>
        <%--                    <select name="type">--%>
        <%--                        <option></option>--%>
        <%--                        @for(var equipment : equipmentTypes)--%>
        <%--                            <option value="${equipment.deploymentId()}">${equipment.name()}</option>--%>
        <%--                        @endfor--%>
        <%--                    </select>--%>
        <%--                </div>--%>
        <%--            </div>--%>
        <%--        </div>--%>
        <div class="field" hidden>
            <label class="label">Id</label>
            <div class="control">
                <input class="input" type="text" name="id" value="0">
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