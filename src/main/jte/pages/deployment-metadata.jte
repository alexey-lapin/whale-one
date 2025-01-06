@import java.util.List
@import com.github.alexeylapin.whaleone.domain.model.DeploymentMetadata
@import org.springframework.validation.BindingResult

@param BindingResult bindingResult = null
@param DeploymentMetadata deploymentMetadata


@template.layout.bulma(content = @`
    <form action="/deployments/${deploymentMetadata.id()}/metadata" method="post">
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
        <div class="field">
            <label class="label">Id</label>
            <div class="control">
                <input class="input" type="text" name="id" value="${deploymentMetadata.id()}" disabled>
            </div>
        </div>
        <div class="field" hidden>
            <label class="label">Version</label>
            <div class="control">
                <input class="input" type="number" name="version" value="${deploymentMetadata.version()}">
            </div>
        </div>
        <div class="field">
            <label class="label">Longitude</label>
            <div class="control">
                <input class="input"
                       type="number"
                       step="any"
                       name="longitude"
                       placeholder="Longitude"
                       value="${deploymentMetadata.longitude()}">
            </div>
        </div>
        <div class="field">
            <label class="label">Latitude</label>
            <div class="control">
                <input class="input"
                       type="number"
                       step="any"
                       name="latitude"
                       placeholder="Latitude"
                       value="${deploymentMetadata.latitude()}">
            </div>
        </div>
        <div class="field">
            <label class="label">Sample Rate</label>
            <div class="control">
                <input class="input"
                       type="number"
                       step="any"
                       name="sampleRate"
                       placeholder="Sample Rate"
                       value="${deploymentMetadata.sampleRate()}">
            </div>
        </div>
        <div class="field">
            <label class="label">Duty Cycle Record</label>
            <div class="control">
                <input class="input"
                       type="number"
                       step="any"
                       name="dutyCycleRecord"
                       placeholder="Duty Cycle Record"
                       value="${deploymentMetadata.dutyCycleRecord()}">
            </div>
        </div>
        <div class="field">
            <label class="label">Duty Cycle Sleep</label>
            <div class="control">
                <input class="input"
                       type="number"
                       step="any"
                       name="dutyCycleSleep"
                       placeholder="Duty Cycle Sleep"
                       value="${deploymentMetadata.dutyCycleSleep()}">
            </div>
        </div>
        <div class="field">
            <label class="label">Duty Cycle Interval</label>
            <div class="control">
                <input class="input"
                       type="number"
                       step="any"
                       name="dutyCycleInterval"
                       placeholder="Duty Cycle Interval"
                       value="${deploymentMetadata.dutyCycleInterval()}">
            </div>
        </div>
        <div class="field">
            <label class="label">Recording Status</label>
            <div class="control">
                <input class="input"
                       type="text"
                       name="recordingStatus"
                       placeholder="Recording Status"
                       value="${deploymentMetadata.recordingStatus()}">
            </div>
        </div>
        <div class="field">
            <label class="label">Recording Status Note</label>
            <div class="control">
                <input class="input"
                       type="text"
                       name="recordingStatusNote"
                       placeholder="Recording Status Note"
                       value="${deploymentMetadata.recordingStatusNote()}">
            </div>
        </div>
        <div class="field is-grouped">
            <div class="control">
                <button class="button is-link">Save</button>
            </div>
            <a href="/deployments/${deploymentMetadata.id()}" class="button is-light">Back</a>
        </div>
    </form>
`)