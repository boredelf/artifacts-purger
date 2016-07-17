package br.com.poupex.maven.extension.artifactspurger;

import br.com.poupex.maven.extension.artifactspurger.purger.ArtifactsPurger;
import br.com.poupex.maven.extension.artifactspurger.util.CollectionsUtils;
import org.apache.maven.AbstractMavenLifecycleParticipant;
import org.apache.maven.MavenExecutionException;
import org.apache.maven.execution.MavenSession;
import org.codehaus.plexus.component.annotations.Component;

import static java.util.Arrays.asList;

@Component(role = AbstractMavenLifecycleParticipant.class)
public class MavenExtension extends AbstractMavenLifecycleParticipant {

	@Override
	public void afterProjectsRead(MavenSession session) throws MavenExecutionException {
		super.afterProjectsRead(session);

		if (CollectionsUtils.hasAny(session.getGoals(), asList("install", "site", "deploy"))) {
			ArtifactsPurger.purge(
				session.getLocalRepository()
					.find(session.getCurrentProject().getArtifact())
					.getFile().getParentFile().getParentFile()
			);
		}

	}

}
