package weave.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import weave.Globals;
import weave.Settings;

public class LegacyUtils extends Globals 
{
	public static void moveV1toV2() throws IOException, InterruptedException
	{
		File oldDir = new File(Settings.APPDATA_DIRECTORY, "WeaveUpdater");
		
		if( oldDir.exists() ) 
		{
			File oldRevs = new File(oldDir, "revisions");
			if( oldRevs.exists() )
				FileUtils.copy(oldRevs, Settings.WEAVE_BINARIES_DIRECTORY, TransferUtils.OVERWRITE | TransferUtils.PRESERVE);
			FileUtils.recursiveDelete(oldDir);
		}
	}
	
	public static void moveRevisionsToDownloads() throws FileNotFoundException, IOException, InterruptedException
	{
		File oldRevisions = new File(Settings.WEAVE_ROOT_DIRECTORY, "revisions");
		
		if( oldRevisions.exists() )
		{
			FileUtils.copy(oldRevisions, Settings.WEAVE_BINARIES_DIRECTORY, TransferUtils.OVERWRITE | TransferUtils.PRESERVE);
			FileUtils.recursiveDelete(oldRevisions);
		}
	}
}
