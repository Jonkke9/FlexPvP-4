package fi.flexplex.pvp;

import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.PMD.StatusCode;
import net.sourceforge.pmd.PMDConfiguration;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

final class TestPMD {

	@Test
	void testJava() {
		final PMDConfiguration config = new PMDConfiguration();

		config.setInputPaths(List.of(
				"src/main/java",
				"src/test/java"
		));

		config.setRuleSets(List.of(
				"src/main/test/resources/pmd-java-ruleset.xml"
		));

		config.setReportFormat("html");
		config.setReportFile("pmd-report.html");

		final StatusCode code = PMD.runPmd(config);
		if (code == StatusCode.OK) {
			try {
				Files.delete(Path.of(config.getReportFile()));
			} catch (final IOException e) {
				fail("Failed to delete pmd-report.html file");
			}
		}

		assertEquals(StatusCode.OK, code, "PDM Java ok");
	}

}
