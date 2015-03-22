package ru.fizteh.fivt.students.ZatsepinMikhail.Telnet.ClientPackage.CommandsClientConnections;

import ru.fizteh.fivt.storage.structured.RemoteTableProviderFactory;
import ru.fizteh.fivt.students.ZatsepinMikhail.Telnet.ClientPackage.CommandExtended;
import ru.fizteh.fivt.students.ZatsepinMikhail.Telnet.ClientPackage.RealRemoteTableProviderFactory;

public class ClientCmdDisconnect extends CommandExtended<RemoteTableProviderFactory> {
    public ClientCmdDisconnect() {
        name = "disconnect";
        numberOfArguments = 1;
    }

    @Override
    public boolean run(RemoteTableProviderFactory factory, String[] args) {
        RealRemoteTableProviderFactory realFactory = (RealRemoteTableProviderFactory) factory;
        if (realFactory.getCurrentProvider() == null) {
            System.out.println("not connected");
        } else {
            realFactory.disconnectCurrentProvider();
            System.out.println("disconnected");
        }
        return true;
    }
}
