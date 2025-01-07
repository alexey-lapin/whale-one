@import com.github.alexeylapin.whaleone.domain.model.Equipment
@import com.github.alexeylapin.whaleone.domain.model.EquipmentType
@import java.util.List
@import java.util.Map
@import org.springframework.boot.info.BuildProperties

@param Equipment equipment
@param Map<Integer, EquipmentType> equipmentTypes
@param BuildProperties buildProperties

@template.layout.bulma(buildProperties = buildProperties, content = @`
    <form action="/equipment/${equipment.id()}" method="post">
        <div class="field">
            <label class="label">Id</label>
            <div class="control">
                <input class="input"
                       type="text"
                       name="id"
                       value="${equipment.id()}"
                       disabled
                >
            </div>
        </div>
        <div class="field">
            <label class="label">Type</label>
            <div class="control">
                <input class="input"
                       type="text"
                       name="type"
                       value="${equipmentTypes.get(equipment.type()).name()}"
                       disabled
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
                       value="${equipment.name()}"
                       required
                >
            </div>
        </div>
        <div class="card">
            <div class="card-content mt-0">
                <div class="content">
                    <p class="is-size-5">📋 Attributes</p>
                    @for(var attribute : equipmentTypes.get(equipment.type()).attributes())
                        <div class="field">
                            <label class="label">${attribute.name()}</label>
                            <div class="control">
                                <input class="input"
                                       type="text"
                                       name="attributes[${attribute.name()}]"
                                       placeholder="Value"
                                       value=""
                                >
                            </div>
                        </div>
                    @endfor
                </div>
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