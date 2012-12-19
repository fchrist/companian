package de.fct.companian.analyze.mvn.helper;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.artifact.repository.ArtifactRepositoryFactory;
import org.apache.maven.artifact.repository.ArtifactRepositoryPolicy;
import org.apache.maven.artifact.repository.DefaultArtifactRepositoryFactory;
import org.apache.maven.artifact.repository.layout.DefaultRepositoryLayout;
import org.apache.maven.cli.MavenLoggerManager;
import org.apache.maven.project.DefaultProjectBuildingRequest;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.ProjectBuilder;
import org.apache.maven.project.ProjectBuildingException;
import org.apache.maven.project.ProjectBuildingRequest;
import org.apache.maven.project.ProjectBuildingResult;
import org.codehaus.plexus.ContainerConfiguration;
import org.codehaus.plexus.DefaultContainerConfiguration;
import org.codehaus.plexus.DefaultPlexusContainer;
import org.codehaus.plexus.PlexusContainerException;
import org.codehaus.plexus.classworlds.ClassWorld;
import org.codehaus.plexus.component.repository.exception.ComponentLookupException;
import org.codehaus.plexus.logging.console.ConsoleLogger;

public class MvnProjectBuilder {
	
	private static Logger logger = Logger.getLogger(MvnProjectBuilder.class);

	public static MavenProject buildMavenProject(File pomFile) {
		if (logger.isDebugEnabled()) {
			logger.debug("buildMavenProject() start, pomFile=" + pomFile);
		}
		MavenProject mvnProject = null;
		
		ClassWorld classWorld = new ClassWorld("plexus.core", Thread.currentThread().getContextClassLoader());

		ContainerConfiguration cc = new DefaultContainerConfiguration().setClassWorld(classWorld).setName("embedder");

		DefaultPlexusContainer container;
		org.codehaus.plexus.logging.Logger mvnLogger;
		try {
			mvnLogger = new ConsoleLogger(org.codehaus.plexus.logging.Logger.LEVEL_DEBUG, "Console");
			container = new DefaultPlexusContainer(cc);
			container.setLoggerManager(new MavenLoggerManager(mvnLogger));
			container.getLoggerManager().setThresholds(org.codehaus.plexus.logging.Logger.LEVEL_DEBUG);

			ProjectBuilder builder;
			try {
				builder = container.lookup(ProjectBuilder.class);
				logger.info("buildMavenProject() project builder = " + builder);

				try {

					ArtifactRepositoryFactory repoFactory = new DefaultArtifactRepositoryFactory();

					ArtifactRepository localRepo = repoFactory.createArtifactRepository("mylocal", "file://h:/maven/repository", new DefaultRepositoryLayout(), new ArtifactRepositoryPolicy(), new ArtifactRepositoryPolicy());

					List<ArtifactRepository> remoteRepos = new ArrayList<ArtifactRepository>();
					ArtifactRepository mvnCentral = repoFactory.createArtifactRepository("mvnCentral", "http://repo1.maven.org/maven2/", new DefaultRepositoryLayout(), new ArtifactRepositoryPolicy(), new ArtifactRepositoryPolicy());
					remoteRepos.add(mvnCentral);

					ProjectBuildingRequest buildRequest = new DefaultProjectBuildingRequest();
					buildRequest.setLocalRepository(localRepo);
					buildRequest.setRemoteRepositories(remoteRepos);
					buildRequest.setResolveDependencies(true);
					buildRequest.setOffline(false);

					ProjectBuildingResult buildResult = builder.build(pomFile, buildRequest);
					if (buildResult != null) {
						logger.info("buildMavenProject() got a build result");

						mvnProject = buildResult.getProject();
					}
				}
				catch (ProjectBuildingException e) {
					logger.error("buildMavenProject() error building project", e);
				}

			}
			catch (ComponentLookupException e) {
				logger.error("buildMavenProject() error looking up ArtifactResolver", e);
			}
		}
		catch (PlexusContainerException e) {
			logger.error("buildMavenProject() error from Plexus container", e);
		}

		if (logger.isDebugEnabled()) {
			logger.debug("buildMavenProject() finished, mvnProject=" + mvnProject);
		}
		return mvnProject;
	}
}
