val spotifyAar = file("spotify-app-remote-release-0.8.0.aar")

if (!spotifyAar.exists()) {
    throw GradleException(
        "Mangler Spotify App Remote SDK. " +
            "Legg 'spotify-app-remote-release-0.8.0.aar' i ${project.projectDir}"
    )
}

configurations.maybeCreate("default")
artifacts.add("default", spotifyAar)
