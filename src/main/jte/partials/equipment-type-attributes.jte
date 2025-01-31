@import com.github.alexeylapin.whaleone.domain.model.EquipmentType

@param EquipmentType equipmentType

<div id="component-equipment-type-attributes">
    <form id="form-equipment-type-attributes"
          hx-post="/equipment/types/${equipmentType.id()}/attributes2"
          hx-trigger="end from:#form-equipment-type-attributes-items"
          hx-swap="none">
    </form>
    <div id="form-equipment-type-attributes-items" class="sortable">
        <%--    <div class="htmx-indicator">Updating...</div>--%>
        @for(var attribute : equipmentType.attributes())
        <%--        <div class="field is-grouped">--%>
        <%--            <div class="control is-flex is-align-items-center">--%>
        <%--                <span class="pi pi-bars sortable-handle"></span>--%>
        <%--            </div>--%>
        <%--            <div class="control is-flex-shrink-4">--%>
        <%--                <input class="input is-static"--%>
        <%--                       type="text"--%>
        <%--                       name="id"--%>
        <%--                       value="${attribute.id()}"--%>
        <%--                       readonly--%>
        <%--                >--%>
        <%--            </div>--%>
        <%--            <div class="control">--%>
        <%--                <input class="input"--%>
        <%--                       type="text"--%>
        <%--                       name="name"--%>
        <%--                       placeholder="Name"--%>
        <%--                       value="${attribute.name()}"--%>
        <%--                       readonly--%>
        <%--                >--%>
        <%--            </div>--%>
        <%--            <button class="button is-danger is-outlined"--%>
        <%--                    hx-delete="/equipment/types/${equipmentType.id()}/attributes/${attribute.id()}"--%>
        <%--                    hx-target="#attributes"--%>
        <%--                    hx-swap="outerHTML">--%>
        <%--                <i class="pi pi-trash"></i>--%>
        <%--            </button>--%>
        <%--        </div>--%>
            @template.partials.equipment-type-attribute(attribute = attribute,
            equipmentTypeId = equipmentType.id(),
            isEditable = false)
        @endfor
    </div>
</div>
