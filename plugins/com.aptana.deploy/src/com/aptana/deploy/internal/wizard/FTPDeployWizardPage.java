package com.aptana.deploy.internal.wizard;

import org.eclipse.core.resources.IProject;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;

import com.aptana.deploy.Activator;
import com.aptana.deploy.preferences.DeployPreferenceUtil;
import com.aptana.ide.core.io.IBaseRemoteConnectionPoint;
import com.aptana.ide.core.io.IConnectionPoint;
import com.aptana.ide.syncing.core.ISiteConnection;
import com.aptana.ide.syncing.core.SiteConnectionUtils;
import com.aptana.ide.syncing.ui.preferences.IPreferenceConstants.SyncDirection;
import com.aptana.ide.syncing.ui.preferences.SyncPreferenceUtil;
import com.aptana.ide.ui.ftp.internal.FTPConnectionPropertyComposite;

@SuppressWarnings("restriction")
public class FTPDeployWizardPage extends WizardPage implements FTPConnectionPropertyComposite.Listener
{

	public static final String NAME = "FTPDeployment"; //$NON-NLS-1$
	private static final String ICON_PATH = "icons/ftp.png"; //$NON-NLS-1$

	private IProject project;
	private FTPDeployComposite ftpConnectionComposite;
	private IBaseRemoteConnectionPoint connectionPoint;

	protected FTPDeployWizardPage(IProject project)
	{
		super(NAME, Messages.FTPDeployWizardPage_Title, Activator.getImageDescriptor(ICON_PATH));
		this.project = project;
		// checks if the project already has an associated FTP connection and fills the info automatically if one exists
		ISiteConnection[] sites = SiteConnectionUtils.findSitesForSource(project, true);
		String lastConnection = DeployPreferenceUtil.getDeployEndpoint(project);
		IConnectionPoint connection;
		for (ISiteConnection site : sites)
		{
			connection = site.getDestination();
			if ((connection != null && connection.getName().equals(lastConnection))
					|| (lastConnection == null && connection instanceof IBaseRemoteConnectionPoint))
			{
				connectionPoint = (IBaseRemoteConnectionPoint) connection;
				break;
			}
		}
	}

	public IBaseRemoteConnectionPoint getConnectionPoint()
	{
		return ftpConnectionComposite.getConnectionPoint();
	}

	public boolean isAutoSyncSelected()
	{
		return ftpConnectionComposite.isAutoSyncSelected();
	}

	public SyncDirection getSyncDirection()
	{
		return ftpConnectionComposite.getSyncDirection();
	}

	public boolean completePage()
	{
		boolean complete = ftpConnectionComposite.completeConnection();
		// persists the auto-sync setting
		boolean autoSync = isAutoSyncSelected();
		SyncPreferenceUtil.setAutoSync(project, autoSync);
		if (autoSync)
		{
			SyncPreferenceUtil.setAutoSyncDirection(project, getSyncDirection());
		}

		return complete;
	}

	@Override
	public void createControl(Composite parent)
	{
		ftpConnectionComposite = new FTPDeployComposite(parent, SWT.NONE, connectionPoint, this);
		ftpConnectionComposite
				.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_FILL | GridData.HORIZONTAL_ALIGN_FILL));
		setControl(ftpConnectionComposite);

		initializeDialogUnits(parent);
		Dialog.applyDialogFont(ftpConnectionComposite);

		ftpConnectionComposite.validate();
	}

	@Override
	public IWizardPage getNextPage()
	{
		return null;
	}

	@Override
	public boolean close()
	{
		return false;
	}

	@Override
	public void error(String message)
	{
		if (message == null)
		{
			setErrorMessage(null);
			setMessage(null);
		}
		else
		{
			setErrorMessage(message);
		}
		setPageComplete(message == null);
	}

	@Override
	public void layoutShell()
	{
	}

	@Override
	public void lockUI(boolean lock)
	{
	}

	@Override
	public void setValid(boolean valid)
	{
	}
}
