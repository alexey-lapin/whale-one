@import org.springframework.boot.info.BuildProperties
@import org.springframework.security.core.context.SecurityContextHolder

@param String username = SecurityContextHolder.getContext().getAuthentication().getName()
@param BuildProperties buildProperties

@template.layout.bulma(buildProperties = buildProperties, content = @`
    <div class="bg-white shadow rounded p-4">
        <h1 class="text-2xl font-bold mb-4">Welcome, ${username}!</h1>
        <p>This is your dashboard. Here you can view recent activity and manage your account.</p>
    </div>
`)