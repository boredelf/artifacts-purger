package br.com.poupex.maven.extension.artifactspurger;

import java.io.File;

import br.com.poupex.maven.extension.artifactspurger.purger.ArtifactsPurger;
import br.com.poupex.maven.extension.artifactspurger.util.CollectionsUtils;
import org.apache.maven.AbstractMavenLifecycleParticipant;
import org.apache.maven.MavenExecutionException;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.component.annotations.Component;

import static java.util.Arrays.asList;

@Component(role = AbstractMavenLifecycleParticipant.class)
public class ArtifactsPurgerMavenExtension extends AbstractMavenLifecycleParticipant {

	@Override
	public void afterProjectsRead(MavenSession session) throws MavenExecutionException {
		super.afterProjectsRead(session);

		if (CollectionsUtils.hasAny(session.getGoals(), asList("install", "site", "deploy"))) {
			MavenProject currentProject = session.getCurrentProject();

			File groupIdDirectory = session.getLocalRepository().find(currentProject.getArtifact())
				.getFile().getParentFile().getParentFile().getParentFile();

			ArtifactsPurger.purge(new File(groupIdDirectory, currentProject.getArtifactId()));
			for (String moduleName : currentProject.getModules()) {
				ArtifactsPurger.purge(new File(groupIdDirectory, moduleName));
			}
		}

	}

}
