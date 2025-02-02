@import com.github.alexeylapin.whaleone.domain.model.Equipment
@import com.github.alexeylapin.whaleone.domain.model.EquipmentType
@import java.util.List
@import org.springframework.boot.info.BuildProperties

@param List<EquipmentType> equipmentTypes
@param BuildProperties buildProperties

@template.layout.bulma-container(buildProperties = buildProperties, content = @`
    <form action="/equipment/new" method="post">
        <div class="field">
            <label class="label">Type</label>
            <div class="control">
                <div class="select">
                    <select name="type" required>
                        <option></option>
                        @for(var equipmentType : equipmentTypes)
                            <option value="${equipmentType.id()}">${equipmentType.name()}</option>
                        @endfor
                    </select>
                </div>
            </div>
        </div>
        <div class="field is-hidden">
            <div class="control">
                <input class="input" type="hidden" name="version" value="0">
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
            <a href="/equipment" class="button is-light">Back</a>
        </div>
    </form>
`)