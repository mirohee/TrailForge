package com.example.trailforge.data

import com.example.trailforge.BuildConfig
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.storage.Storage

// Supabase client provider to create a Supabase client instance

object SupabaseClientProvider {

    val supabase = createSupabaseClient(
        supabaseUrl = BuildConfig.SUPABASE_URL,
        supabaseKey = BuildConfig.SUPABASE_API_KEY
    ) {
        install(Auth)
        install(Postgrest)
        install(Storage)
    }
}
