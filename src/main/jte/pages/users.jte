@import com.github.alexeylapin.whaleone.domain.model.User
@import com.github.alexeylapin.whaleone.domain.model.Page
@import org.springframework.boot.info.BuildProperties

@param Page<User> users
@param BuildProperties buildProperties

@template.layout.bulma(buildProperties = buildProperties, content = @`
    <a href="/users/new" class="button">
            <span class="icon">
                <i class="pi pi-plus"></i>
            </span>
        <span>New</span>
    </a>
    <table class="table is-hoverable is-fullwidth">
        <thead>
        <tr>
            <th>Name</th>
            <th>Is Enabled</th>
            <th>Actions</th>
        </tr>
        </thead>
        <tbody>
        @for (var user : users.items())
            <tr>
                <td>${user.username()}</td>
                <td>${user.enabled()}</td>
                <td>
                    <a href="/users/${user.id()}" class="button is-small">edit</a>
                </td>
            </tr>
        @endfor
        </tbody>
    </table>
`)