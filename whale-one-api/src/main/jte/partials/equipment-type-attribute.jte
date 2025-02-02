@param com.github.alexeylapin.whaleone.domain.model.EquipmentTypeAttribute attribute
@param long equipmentTypeId
@param boolean isEditable

<div id="attribute-${attribute.id()}" class="card">
    <form method="post" action="/equipment/types/${equipmentTypeId}/attributes/${attribute.id()}">
        <input name="id" type="hidden" value="${attribute.id()}" form="form-equipment-type-attributes">
        <div class="card-content">
            <div class="is-flex">
                <div class="is-flex is-align-items-center is-flex-grow-1">
                    <button class="button is-small sortable-handle">
                    <span class="icon">
                        <i class="pi pi-bars"></i>
                    </span>
                    </button>
                    <p class="ml-4"># ${attribute.id()}</p>
                </div>
                @if(!isEditable)
                    <div>
                        <button class="button is-small"
                                hx-get="/equipment/types/${equipmentTypeId}/attributes/${attribute.id()}?editable=true"
                                hx-target="#attribute-${attribute.id()}"
                                hx-swap="outerHTML">
                        <span class="icon">
                            <i class="pi pi-pencil"></i>
                        </span>
                        </button>
                        <button class="button is-small"
                                hx-delete="/equipment/types/${equipmentTypeId}/attributes/${attribute.id()}"
                                hx-target="#component-equipment-type-attributes"
                                hx-swap="outerHTML">
                        <span class="icon has-text-danger">
                            <i class="pi pi-trash"></i>
                        </span>
                        </button>
                    </div>
                @endif
            </div>
            <div class="mt-2">
                <div class="columns mb-0">
                    <div class="column is-one-quarter">
                        <div class="field">
                            <label class="label">Type</label>
                            <div class="control is-expanded">
                                <div class="select is-fullwidth">
                                    <select name="type"
                                            required
                                            disabled="${!isEditable}">
                                        <option></option>
                                        <option>file</option>
                                        <option>number</option>
                                        <option>text</option>
                                        <option>textarea</option>
                                    </select>
                                </div>
                            </div>
                        </div>
                    </div>
                    <div class="column">
                        <div class="field">
                            <label class="label">Name</label>
                            <div class="control">
                                <input class="input"
                                       type="text"
                                       name="name"
                                       placeholder="Name"
                                       value="${attribute.name()}"
                                       disabled="${!isEditable}"
                                >
                            </div>
                        </div>
                    </div>
                </div>
                <div class="field">
                    <label class="label">Description</label>
                    <div class="control">
                        <input class="input"
                               type="text"
                               name="description"
                               placeholder="Description"
                               value=""
                               disabled="${!isEditable}"
                        >
                    </div>
                </div>
                @if(isEditable)
                    <div class="field is-grouped">
                        <div class="control">
                            <button class="button is-link is-small" type="submit">Save</button>
                        </div>
                        <a href="/equipment" class="button is-light is-small"
                           hx-get="/equipment/types/${equipmentTypeId}/attributes/${attribute.id()}?editable=false"
                           hx-target="#attribute-${attribute.id()}"
                           hx-swap="outerHTML">Cancel</a>
                    </div>
                @endif
            </div>
        </div>
    </form>
</div>