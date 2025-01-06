@import com.github.alexeylapin.whaleone.domain.model.EquipmentType
@import com.github.alexeylapin.whaleone.domain.model.EquipmentTypeAttribute
@import gg.jte.support.ForSupport

@param EquipmentType equipmentType

@template.layout.bulma(content = @`
    <form action="/equipment/types/${equipmentType.id()}" method="post">
        <div class="field">
            <label class="label">Id</label>
            <div class="control">
                <input class="input"
                       type="text"
                       name="id"
                       value="${equipmentType.id()}"
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
                       value="${equipmentType.name()}"
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

    <div class="card mt-4">
        <div class="card-content">
            <div class="content">
                <p class="is-size-5">📋 Attributes</p>
                @template.partials.equipment-type-attributes(equipmentType = equipmentType)
                <form action="/equipment/types/${equipmentType.id()}/attributes" method="post" class="mt-2">
                    <div class="field is-grouped">
                        <div class="control" hidden="hidden">
                            <input class="input"
                                   type="number"
                                   name="id"
                                   value="0"
                            >
                        </div>
                        <div class="control">
                            <input class="input"
                                   type="text"
                                   name="name"
                                   placeholder="Name"
                                   required
                            >
                        </div>
                        <div class="control">
                            <button class="button is-link">Add</button>
                        </div>
                    </div>
                </form>
            </div>
        </div>
    </div>
`)