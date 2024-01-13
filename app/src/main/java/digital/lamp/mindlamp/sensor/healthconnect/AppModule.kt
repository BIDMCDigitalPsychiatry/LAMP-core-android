package digital.lamp.mindlamp.sensor.healthconnect

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import digital.lamp.mindlamp.app.App
import java.util.UUID

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
  /*  @Provides
    fun provideSomeString(): String {
        return UUID.randomUUID().toString()
    }

    @Provides
    fun provideHealthManager():GoogleHealthConnect{
        return App.app.healthConnectManager
    }*/
}