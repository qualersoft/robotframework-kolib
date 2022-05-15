import de.qualersoft.robotframework.library.RobotLib
import de.qualersoft.robotframework.library.example.TaMarker

@Suppress("unused")
open class KExampleLib(vararg args: String) : RobotLib(TaMarker::class, *args) {

  override fun getLibraryGeneralDocumentation() = "General documentation of KExampleLib"
  override fun getLibraryUsageDocumentation() = "Usage documentation of KExampleLib"

  companion object {
    const val ROBOT_LIBRARY_SCOPE: String = "GLOBAL"
    const val ROBOT_LIBRARY_VERSION: String = "1.0.0"
  }
}
