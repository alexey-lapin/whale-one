@import com.github.alexeylapin.whaleone.domain.model.EquipmentType
@import com.github.alexeylapin.whaleone.domain.model.EquipmentTypeAttribute
@import java.time.format.DateTimeFormatter
@import org.springframework.boot.info.BuildProperties

@param EquipmentType equipmentType
@param DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
@param BuildProperties buildProperties

@template.layout.bulma-container(buildProperties = buildProperties, content = @`
    <form action="/equipment/types/${equipmentType.id()}" method="post">
        <div class="columns">
            <div class="column">
                <div class="field">
                    <label class="label">Id</label>
                    <div class="control">
                        <input class="input"
                               type="text"
                               name="id"
                               value="${equipmentType.id()}"
                               disabled
                        >
                        <input type="hidden"
                               name="version"
                               value="${equipmentType.version()}"
                        >
                    </div>
                </div>
            </div>
            <div class="column">
                <div class="field">
                    <label class="label">Created At</label>
                    <div class="control">
                        <input class="input"
                               type="text"
                               name="createdAt"
                               value="${dateTimeFormatter.format(equipmentType.createdAt())}"
                               disabled
                        >
                    </div>
                </div>
            </div>
            <div class="column">
                <div class="field">
                    <label class="label">Created By</label>
                    <div class="control">
                        <input class="input"
                               type="text"
                               name="createdBy"
                               value="${equipmentType.createdBy()}"
                               disabled
                        >
                        <input type="hidden"
                               name="createdById"
                               value="${equipmentType.createdById()}"
                        >
                    </div>
                </div>
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