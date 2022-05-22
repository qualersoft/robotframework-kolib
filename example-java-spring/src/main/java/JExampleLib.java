import de.qualersoft.robotframework.library.RobotLib;
import de.qualersoft.robotframework.library.example.TaMarker;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("unused")
public class JExampleLib extends RobotLib {

  public JExampleLib(String... args) {
    super(TaMarker.class, args);
  }

  @NotNull
  @Override
  public String getLibraryGeneralDocumentation() {
    return "General documentation of JExampleLib";
  }

  @NotNull
  @Override
  public String getLibraryUsageDocumentation() {
    return "Usage documentation of JExampleLib";
  }

  public static final String ROBOT_LIBRARY_SCOPE = "GLOBAL";
}
