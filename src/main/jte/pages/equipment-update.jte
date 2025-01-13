@import com.github.alexeylapin.whaleone.domain.model.Equipment
@import com.github.alexeylapin.whaleone.domain.model.EquipmentAttribute
@import com.github.alexeylapin.whaleone.domain.model.EquipmentType
@import java.time.format.DateTimeFormatter
@import java.util.Map
@import java.util.Optional
@import gg.jte.support.ForSupport
@import org.springframework.boot.info.BuildProperties

@param Equipment equipment
@param EquipmentType equipmentType
@param Map<Long, EquipmentAttribute> equipmentAttributeMap
@param DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
@param BuildProperties buildProperties

@template.layout.bulma-container(buildProperties = buildProperties, content = @`
    <form action="/equipment/${equipment.id()}" method="post">
        <div class="columns">
            <div class="column">
                <div class="field">
                    <label class="label">Id</label>
                    <div class="control">
                        <input class="input"
                               type="text"
                               name="id"
                               value="${equipment.id()}"
                               disabled
                        >
                        <input type="hidden"
                               name="version"
                               value="${equipment.version()}"
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
                               value="${dateTimeFormatter.format(equipment.createdAt())}"
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
                               value="${equipment.createdBy()}"
                               disabled
                        >
                        <input type="hidden"
                               name="createdById"
                               value="${equipment.createdById()}"
                        >
                    </div>
                </div>
            </div>
        </div>
        <div class="field">
            <label class="label">Type</label>
            <div class="field has-addons">
                <p class="control is-expanded">
                    <input class="input"
                           type="text"
                           name="type"
                           value="${equipmentType.name()}"
                           disabled
                    >
                </p>
                <p class="control">
                    <a href="/equipment/types/${equipment.type()}"
                       class="button"
                    >
                    <span class="icon">
                        <i class="pi pi-external-link"></i>
                    </span>
                    </a>
                </p>
            </div>
        </div>
        <div class="field">
            <label class="label">Allocated For</label>
            <div class="field has-addons">
                <p class="control is-expanded">
                    <input class="input"
                           type="text"
                           name="type"
                           value="${equipment.deploymentId()}"
                           disabled
                    >
                </p>
                @if(equipment.deploymentId() != null)
                    <p class="control">
                        <a href="/deployments/${equipment.deploymentId()}"
                           class="button"
                        >
                    <span class="icon">
                        <i class="pi pi-external-link"></i>
                    </span>
                        </a>
                    </p>
                @endif
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
                    @for(var typeAttribute : ForSupport.of(equipmentType.attributes()))
                        <input type="hidden" name="attributes[${typeAttribute.getIndex()}].id" value="0">
                        <input type="hidden" name="attributes[${typeAttribute.getIndex()}].equipmentTypeAttributeId" value="${typeAttribute.get().id()}">
                        <div class="field">
                            <label class="label">${typeAttribute.get().name()}</label>
                            <div class="control">
                                <input class="input"
                                       type="text"
                                       name="attributes[${typeAttribute.getIndex()}].value"
                                       placeholder="Value"
                                       value="${Optional.ofNullable(equipmentAttributeMap.get(typeAttribute.get().id())).map(i -> i.value()).orElse(null)}"
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