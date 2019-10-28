import java.util.Map;

public interface FileCompressor {
	boolean encode ( String input_filename, int level, boolean reset, String output_filename );
	boolean decode ( String input_filename, String output_filename );
	Map<Character, String> codebook ( );
}
