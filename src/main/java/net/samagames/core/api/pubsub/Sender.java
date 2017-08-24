package net.samagames.core.api.pubsub;

import net.samagames.api.pubsub.ISender;
import net.samagames.api.pubsub.PendingMessage;
import net.samagames.core.APIPlugin;
import net.samagames.core.ApiImplementation;
import redis.clients.jedis.Jedis;

import java.util.concurrent.LinkedBlockingQueue;

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
class Sender implements Runnable, ISender
{

    private final LinkedBlockingQueue<PendingMessage> pendingMessages = new LinkedBlockingQueue<>();
    private final ApiImplementation connector;
    private Jedis jedis;

    public Sender(ApiImplementation connector)
    {
        this.connector = connector;
    }

    public void publish(PendingMessage message)
    {
        pendingMessages.add(message);
    }

    @Override
    public void run()
    {
        fixDatabase();
        while (true)
        {
            PendingMessage message;
            try
            {
                message = pendingMessages.take();
            } catch (InterruptedException e)
            {
                e.printStackTrace();
                jedis.close();
                return;
            }

            boolean published = false;
            while (!published)
            {
                try
                {
                    jedis.publish(message.getChannel(), message.getMessage());
                    message.runAfter();
                    published = true;
                } catch (Exception e)
                {
                    fixDatabase();
                }
            }
        }
    }

    private void fixDatabase()
    {
        try
        {
            jedis = connector.getBungeeResource();
        } catch (Exception e)
        {
            APIPlugin.getInstance().getLogger().severe("[Publisher] Cannot connect to redis server : " + e.getMessage() + ". Retrying in 5 seconds.");
            try
            {
                Thread.sleep(5000);
                fixDatabase();
            } catch (InterruptedException e1)
            {
                e1.printStackTrace();
            }
        }
    }
}
