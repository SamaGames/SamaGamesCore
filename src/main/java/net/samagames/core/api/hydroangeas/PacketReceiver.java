package net.samagames.core.api.hydroangeas;

import net.samagames.core.APIPlugin;
import net.samagames.core.api.hydroangeas.connection.Packet;
import net.samagames.core.api.hydroangeas.packets.PacketCallBack;

import java.util.ArrayList;
import java.util.List;

/*
 * This file is part of SamaGamesCore.
 *
 * SamaGamesCore is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * SamaGamesCore is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with SamaGamesCore.  If not, see <http://www.gnu.org/licenses/>.
 */
public class PacketReceiver
{
    private final APIPlugin plugin;
    private final List<PacketCallBack> callbacks;

    public PacketReceiver(APIPlugin plugin)
    {
        this.plugin = plugin;
        this.callbacks = new ArrayList<>();
    }

    public void registerCallBack(PacketCallBack callBack)
    {
        this.callbacks.add(callBack);
    }

    public void clearCallbacks()
    {
        this.callbacks.clear();
    }

    public void callPacket(Packet packet)
    {
        this.callbacks.stream().filter(callBack -> callBack.getPacketClass().getName().equals(packet.getClass().getName())).forEach(callBack -> this.plugin.getExecutor().execute(() -> callBack.call(packet)));
    }
}
