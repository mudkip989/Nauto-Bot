# bot.py
import os
import random, time, discord, json, io, youtube_dl
from dotenv import load_dotenv

# 1
from discord.ext import commands

random.seed(time.time())
load_dotenv()
TOKEN = os.getenv('DISCORD_TOKEN')
direct = os.path.dirname(__file__)
# Function Names
bcmd = ["cflip", "help", "setprefix", "roll", "play", "pause", "stop", "resume", "join", "leave"]
bcmdh = [" - Flips a coin.", " - Runs help command (Can be done using bot ping instead of prefix)", " <prefix> - (Admin)Sets the bot's prefix in this server (Can be done using bot ping instead of prefix)", " [dSides] - Rolls a die with (Optional)specified sides.", " <Youtube URL> - Plays audio in vc, this is a test", " - Pauses current audio.", " - Stops the current song", " - Resumes the current song", " - Joins the user's vc", " - Leaves the current vc"]
# 2
bot = commands.Bot(command_prefix='^')

youtube_dl.utils.bug_reports_message = lambda: ''
ytdl_format_options = {
    'format': 'bestaudio/best',
    'restrictfilenames': True,
    'noplaylist': True,
    'nocheckcertificate': True,
    'ignoreerrors': False,
    'logtostderr': False,
    'quiet': True,
    'no_warnings': True,
    'default_search': 'auto',
    'source_address': '0.0.0.0' # bind to ipv4 since ipv6 addresses cause issues sometimes
}
ffmpeg_options = {
    'options': '-vn'
}
ytdl = youtube_dl.YoutubeDL(ytdl_format_options)
class YTDLSource(discord.PCMVolumeTransformer):
    def __init__(self, source, *, data, volume=0.5):
        super().__init__(source, volume)
        self.data = data
        self.title = data.get('title')
        self.url = ""
    @classmethod
    async def from_url(cls, url, *, loop=None, stream=False):
        loop = loop or asyncio.get_event_loop()
        data = await loop.run_in_executor(None, lambda: ytdl.extract_info(url, download=not stream))
        if 'entries' in data:
            # take first item from a playlist
            data = data['entries'][0]
        filename = data['title'] if stream else ytdl.prepare_filename(data)
        return filename


# Bot Read Message Event
@bot.event
async def on_message(message):
    if message.author.id != 619206087031390234:
        channel = message.channel
        guild = message.guild
        prefix = getSetting(guild.id, "prefix")
        if prefix == 'null':
            prefix = '*'
            setSetting(guild.id, "prefix", '*')
        prefixlength = len(prefix)
        # Check If Commands
        if message.content.startswith(prefix):
            mtext = "".join(message.content[prefixlength:])
            cmdargs = mtext.split(' ')
            if cmdargs[0] in bcmd:
                func = eval(cmdargs[0] + '(channel, message, cmdargs)')
                await func
            
        # Not Commands
        else:
            cmdargs = message.content
            
            try:
                
                if cmdargs.startswith(guild.get_member(619206087031390234).mention.replace("!", "", 1)):
                    
                    length = len(guild.get_member(619206087031390234).mention.replace("!", "", 1))
                    mtext = message.content[length:].lstrip(' ').split(' ')
                    match mtext[0]:
                        case "help":
                            await help(channel, message, [])
                        case "setprefix":
                            await setprefix(channel, message, mtext)
                        case _:
                            boop = 1
                    
            except:
                print(e)









##@bot.command(name='cflip', help='Flips a coin.')
## 
        
##@bot.command(name='roll', help='Rolls a Die lol.')
##
##
##
##@bot.command(name='create-channel', help='Creates a new channel')
##@commands.has_role('Admin')
##async def create_channel(ctx, channel_name='real-python'):
##    guild = ctx.guild
##    existing_channel = discord.utils.get(guild.channels, name=channel_name)
##    if not existing_channel:
##        print(f'Creating a new channel: {channel_name}')
##        await guild.create_text_channel(channel_name)
##        await ctx.send('Successfully created #' + channel_name)
##
##
##@bot.command(name='create-vc', help='Creates a new vc')
##@commands.has_role('Admin')
##async def create_channel(ctx, channel_name='real-python'):
##    guild = ctx.guild
##    existing_channel = discord.utils.get(guild.channels, name=channel_name)
##    if not existing_channel:
##        print(f'Creating a new channel: {channel_name}')
##        await guild.create_voice_channel(channel_name)
##        await ctx.send('Successfully created vc ' + channel_name)
##
##
##
##
##
##
##@bot.command(name='setquotesin', help='Assigns a channel to find quotes')
##async def set_quotes_in(ctx, quote_channel):
##    if ctx.author.guild_permissions.administrator:
##        setSetting(str(ctx.guild.id), "quotes", ctx.message.channel_mentions[0].mention)
##        await ctx.send(f'Testing command send with channel: {ctx.message.channel_mentions[0].mention}')
##    else:
##        await ctx.send('You are missing the priveledge to do this.')
##
##@bot.command(name='invite', help='Gives invite link for if you want to add bot. Use a 53-bit integer representing the offered permission level.')
##async def invbot(ctx, permCode=8):
##    fakePerm = discord.Permissions(permCode)
##    await ctx.send(discord.utils.oauth_url(619206087031390234, permissions= fakePerm))
##
##
##
##
##

# Bot Command Functions/Methods


async def join(ctx, msg, kargs):
    if not msg.author.voice:
        await ctx.send("{} is not connected to a voice channel".format(msg.author.name))
        return
    else:
        channel = msg.author.voice.channel
    await channel.connect()

async def leave(ctx, msg, kargs):
    voice_client = msg.guild.voice_client
    if voice_client.is_connected():
        await voice_client.disconnect()
        voice_client.cleanup()
    else:
        await ctx.send("The bot is not connected to a voice channel.")


async def play(ctx, msg, kargs):
    try :
        url = ''.join(kargs)[4:]
        print(url)
        server = msg.guild
        voice_channel = server.voice_client
        async with ctx.typing():
            filename = await YTDLSource.from_url(url, loop=bot.loop)
            voice_channel.play(discord.FFmpegPCMAudio(executable="C:/ffmpeg/bin/ffmpeg.exe", source=filename))
        await ctx.send('**Now playing:** {}'.format(filename))
    except:
        await ctx.send("The bot is not connected to a voice channel.")
        print(error)

        

async def resume(ctx, msg, kargs):
    voice_client = msg.guild.voice_client
    if voice_client.is_paused():
        voice_client.resume()
    else:
        await ctx.send("The bot was not playing anything before this. Use play_song command")


async def pause(ctx, msg, kargs):
    voice_client = msg.guild.voice_client
    if voice_client.is_playing():
        voice_client.pause()
    else:
        await ctx.send("The bot is not playing anything at the moment.")


async def stop(ctx, msg, kargs):
    voice_client = msg.guild.voice_client
    if voice_client.is_playing():
        voice_client.stop()
    else:
        await ctx.send("The bot is not playing anything at the moment.")


async def roll(ctx, msg, kargs):
    dSides = 6
    if len(kargs) >= 2:
        try:
            dSides = int(kargs[1])
        except:
            await ctx.send("dSides was not set to an integer number")
    await ctx.send(f'I rolled a {dSides}-sided die:')
    await ctx.send(str(random.randint(1,dSides)))


async def deletedMessages(ctx, msg, kargs):
    try:
        logs = ctx.guild.audit_logs(limit=10, user=ctx.guild.members.get_member(msg.mentions[0]))
    except:
        logs = ctx.guild.audit_logs(limit=10, action=message_delete)

async def cflip(ctx, msg, kargs):
    if random.randint(0,1) == 1:
        await ctx.send('Your Result:\nHeads')
    else:
        
        await ctx.send('Your Result:\nTails')

async def setprefix(ctx, msg, kargs):
    if msg.author.guild_permissions.administrator:
        prefix = kargs[1]
        setSetting(ctx.guild.id, "prefix", prefix)
        await ctx.send(f'Prefix is now {prefix}')
    else:
        await ctx.send("You do not have the permission(Admin) to do this.")
    

async def help(ctx, msg, kargs):
    prefix = getSetting(ctx.guild.id, "prefix")
    await ctx.send("Prefix is " + prefix)
    num = len(bcmd)
    text = "```"
    for x in range(0, num):
        text = text + f'\n   {bcmd[x]}{bcmdh[x]}'
    text = text + '\n```'
    await ctx.send(text)






# Bot Utility Methods

def getSetting(gID, setKey):
    try:
        fil = open(direct + '\guilds.json', 'x')
        fil.close()
    except:
        print("file exists")
    gSetFile = open(direct + '\guilds.json', 'rt')
    gJson = gSetFile.read()
    if gJson == '':
        gJson = '{}'
    gSetFile.close()
    gDict = json.loads(gJson)
    try:
        gSet = gDict[str(gID)]
        getValue = gSet[setKey]
    except:
        getValue = 'null'
    return getValue

def setSetting(gID, setKey, newValue):
    try:
        fil = open(direct + '\guilds.json', 'x')
        fil.close()
    except:
        print("file exists")
    gSetFile = open(direct + '\guilds.json', 'rt')
    gJson = gSetFile.read()
    if gJson == '':
        gJson = '{}'
    gSetFile.close()
    gDict = json.loads(gJson)
    try:
        gSet = gDict[str(gID)]
        gSet.update({setKey: newValue})
    except:
        gSet = {setKey: newValue}
    gDict.update({gID: gSet})
    gJson = json.dumps(gDict)
    gSetFile = open(direct + '\guilds.json', 'w')
    gSetFile.write(gJson)
    gSetFile.close()










# Bot Online Event

@bot.event
async def on_ready():
    print(f'{bot.user.name} has connected to Discord!')
    await bot.change_presence(activity=discord.Activity(type=discord.ActivityType.watching,name='for mudkip989\'s code updates.'))



bot.run(TOKEN)
