package sockyProxy;
import burp.api.montoya.http.message.requests.*;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import burp.api.montoya.MontoyaApi;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public final class Utils {
	private static MontoyaApi api;
	
	public final static boolean isPythonInstalled() {
        try {
            Process process;
            String os = System.getProperty("os.name").toLowerCase();

            if (os.contains("win")) {
                // Windows
                process = new ProcessBuilder("cmd.exe", "/c", "python3", "--version").start();
            } else {
                // Unix-like systems (Linux, macOS)
                process = new ProcessBuilder("python3", "--version").start();
            }

            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line = reader.readLine();
            reader.close();

            // Check if the output contains the Python version information
            return line != null && line.toLowerCase().contains("python");
        } catch (IOException e) {
            // An error occurred while executing the command
            return false;
        }
    }

 public final static void write2file() {
        String filePath = "sockyProxyServer.py";

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            writer.write("import argparse\n" +
            		"from aiohttp import web\n" +
            		"import aiohttp\n" +
            		"import asyncio\n" +
            		"import websockets\n" +
            		"async def handle(request):\n" +
            		"    if request.method == 'GET':\n" +
            		"        return web.Response(text='Please use POST method instead of GET')\n" +
            		"    if request.method == 'POST':\n" +
            		"        payload = await request.text()\n" +
            		"        async with websockets.connect(request.app['ws_url']) as websocket:\n" +
            		"            await websocket.send(payload)\n" +
            		"            try:\n" +
            		"                ws_response = await asyncio.wait_for(websocket.recv(), timeout=5)\n" +
            		"            except asyncio.TimeoutError:\n" +
            		"                ws_response = 'No response received in 5 seconds'\n" +
            		"        return web.Response(text=ws_response)\n" +
            		"def parse_arguments():\n" +
            		"    parser = argparse.ArgumentParser()\n" +
            		"    parser.add_argument('ws_url', help='The WebSocket URL to connect to')\n" +
            		"    parser.add_argument('port', type=int, help='The port number to start the HTTP server on')\n" +
            		"    return parser.parse_args()\n" +
            		"async def main():\n" +
            		"    args = parse_arguments()\n" +
            		"    app = web.Application()\n" +
            		"    app['ws_url'] = args.ws_url\n" +
            		"    app.router.add_route('*', '/', handle)\n" +
            		"    runner = aiohttp.web.AppRunner(app)\n" +
            		"    await runner.setup()\n" +
            		"    site = aiohttp.web.TCPSite(runner, '0.0.0.0', args.port)\n" +
            		"    await site.start()\n" +
            		"    print(f'Serving on port {args.port}')\n" +
            		"    while True:\n" +
            		"        await asyncio.sleep(3600)\n" +
            		"if __name__ == '__main__':\n" +
            		"    asyncio.run(main())\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
 public final static boolean fileExist(String filename) {
        File file = new File(filename);
        if (file.exists()) {
            return true;
        } else {
            return false;
        }
    }
 
 public class ProcessLauncher {
     public static Process process;

     public static void Launch(MontoyaApi api, String port, String url) {
     	try {
             // Command to launch the never-ending process
             String command = "python3";
             
             url = url.replaceFirst("https", "wss");
             url = url.replaceFirst("http", "ws");
             // Create the process builder
             ProcessBuilder pb = new ProcessBuilder(command, "sockyProxyServer.py", url, port);

             // Redirect the error stream to the standard output
             pb.redirectErrorStream(true);

             // Start the process
             process = pb.start();

             // Start a separate thread to handle process termination
             Thread terminationThread = new Thread(() -> {
                 // Wait for the process to finish
                 try {
                     process.waitFor();
                 } catch (InterruptedException e) {
                     e.printStackTrace();
                 }

                 // Perform any cleanup or handling after process termination
             });
             terminationThread.start();

             // Start a separate thread to read the process output
             Thread outputThread = new Thread(() -> {
                 try (InputStream inputStream = process.getInputStream();
                      BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
                     String line;
                     while ((line = reader.readLine()) != null) {
                         api.logging().logToOutput(line);
                     }
                 } catch (IOException e) {
                     e.printStackTrace();
                 }
             });
             outputThread.start();

             // Continue executing the rest of your program here...

             // To kill the process at any time, call the destroy() method
             // process.destroy();
         } catch (IOException e) {
             e.printStackTrace();
         }
     }
     
 }

 public final static void LaunchProxy(MontoyaApi api, String port, String url) {

	 	// check if proxy server on disk
	    if (!Utils.fileExist("sockyProxyServer.py")) {
	    	Utils.write2file();
	    }
	    api.logging().logToOutput("Websocket Proxy Server Started on port (" + port + ") and is redirecting to " + url);
	    ProcessLauncher.Launch(api,port,url);	            
	 }
 


}
