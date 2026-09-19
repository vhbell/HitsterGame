val spotifyAar = file("spotify-auth-release-2.1.0.aar")

if (!spotifyAar.exists()) {
    throw GradleException(
            "Mangler Spotify App Remote SDK. " +
                    "Legg 'spotify-auth-release-2.1.0.aar' i ${project.projectDir}"
    )
}

configurations.maybeCreate("default")
artifacts.add("default", spotifyAar)