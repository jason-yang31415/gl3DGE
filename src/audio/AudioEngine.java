package audio;

import static org.lwjgl.openal.AL10.alDopplerFactor;
import static org.lwjgl.openal.AL10.alDopplerVelocity;
import static org.lwjgl.openal.ALC10.ALC_DEFAULT_DEVICE_SPECIFIER;
import static org.lwjgl.openal.ALC10.ALC_FREQUENCY;
import static org.lwjgl.openal.ALC10.ALC_REFRESH;
import static org.lwjgl.openal.ALC10.ALC_SYNC;
import static org.lwjgl.openal.ALC10.ALC_TRUE;
import static org.lwjgl.openal.ALC10.alcCloseDevice;
import static org.lwjgl.openal.ALC10.alcCreateContext;
import static org.lwjgl.openal.ALC10.alcDestroyContext;
import static org.lwjgl.openal.ALC10.alcGetInteger;
import static org.lwjgl.openal.ALC10.alcGetString;
import static org.lwjgl.openal.ALC10.alcMakeContextCurrent;
import static org.lwjgl.openal.ALC11.ALC_ALL_DEVICES_SPECIFIER;
import static org.lwjgl.openal.ALC11.ALC_MONO_SOURCES;
import static org.lwjgl.openal.ALC11.ALC_STEREO_SOURCES;
import static org.lwjgl.system.MemoryUtil.NULL;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.List;

import org.lwjgl.openal.AL;
import org.lwjgl.openal.ALC;
import org.lwjgl.openal.ALC10;
import org.lwjgl.openal.ALCCapabilities;
import org.lwjgl.openal.ALUtil;

public class AudioEngine {

	private static long device;
	private static ALCCapabilities deviceCaps;
	private static long context;
	
	public static void initAudioEngine(){
		device = ALC10.alcOpenDevice((ByteBuffer) null);
		if (device == NULL)
			throw new IllegalStateException("Failed to open default audio device");
		
		deviceCaps = ALC.createCapabilities(device);
		if (!deviceCaps.OpenALC10)
			throw new IllegalStateException("Device is not OpenALC10 capable.");
		
		System.out.println("OpenALC10: " + deviceCaps.OpenALC10);
		System.out.println("OpenALC11: " + deviceCaps.OpenALC11);
		System.out.println("caps.ALC_EXT_EFX = " + deviceCaps.ALC_EXT_EFX);
		
		if (deviceCaps.OpenALC11){
			List<String> devices = ALUtil.getStringList(NULL, ALC_ALL_DEVICES_SPECIFIER);
			if (devices == null)
				System.err.println("Whoops, something went wrong with the audio device."); //more specific error
			else {
				for (int i = 0; i < devices.size(); i++)
					System.out.println(i + ": " + devices.get(i));
			}
		}
		
		String defaultDeviceSpecifier = alcGetString(NULL, ALC_DEFAULT_DEVICE_SPECIFIER);
		if (defaultDeviceSpecifier == null)
			System.err.println("Whoops, something went wrong with the audio device."); //more specific error
		System.out.println("Default audio device: " + defaultDeviceSpecifier);
		
		context = alcCreateContext(device, (IntBuffer) null);
		alcMakeContextCurrent(context);
		AL.createCapabilities(deviceCaps);
		
		System.out.println("ALC_FREQUENCY: " + alcGetInteger(device, ALC_FREQUENCY) + "Hz");
		System.out.println("ALC_REFRESH: " + alcGetInteger(device, ALC_REFRESH) + "Hz");
		System.out.println("ALC_SYNC: " + (alcGetInteger(device, ALC_SYNC) == ALC_TRUE));
		System.out.println("ALC_MONO_SOURCES: " + alcGetInteger(device, ALC_MONO_SOURCES));
		System.out.println("ALC_STEREO_SOURCES: " + alcGetInteger(device, ALC_STEREO_SOURCES));
	}
	
	public static void destroyAudioEngine(){
		alcDestroyContext(context);
		alcCloseDevice(device);
	}
	
	public static void setDopplerFactor(float factor){
		alDopplerFactor(factor);
	}
	
	public static void setDopplerVelocity(float velocity){
		alDopplerVelocity(velocity);
	}
	
}
