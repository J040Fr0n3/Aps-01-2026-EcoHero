package engine;

import java.net.URL;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;



public class Sound {
	
	Clip clip;
	URL soundURL[]= new URL[30];
	
	public Sound() {
		soundURL[0] = getClass().getResource("/sounds/trampolim_sound.wav");
		soundURL[1] = getClass().getResource("/sounds/damage_sound.wav");
		soundURL[2] = getClass().getResource("/sounds/inwater_sound.wav");
		soundURL[3] = getClass().getResource("/sounds/elevator_sound.wav");
		soundURL[4] = getClass().getResource("/sounds/pickedUp_sound.wav");
		soundURL[5] = getClass().getResource("/sounds/walking_normal_sound.wav");
		soundURL[6] = getClass().getResource("/sounds/walking_plataform_sound.wav");
		soundURL[7] = getClass().getResource("/sounds/walking_sky_sound.wav");
		//soundURL[] = getClass().getResource("/sounds/.wav");
	}
	
	public void setFile(int i) {
		try {
			AudioInputStream ais = AudioSystem.getAudioInputStream(soundURL[i]);
			clip = AudioSystem.getClip();
			clip.open(ais);
	} catch (Exception e) {
		System.out.println("Erro ao carregar o arquivo de som" + e.getMessage());
			}
	}
	
	public void play() {
		if (clip != null) {
			clip.start();
		}
	}
	
	public void loop() {
		if (clip != null) {
			clip.loop(Clip.LOOP_CONTINUOUSLY);
		}
	}
	
	public void stop() {
		if (clip != null) {
			clip.stop();
		}
	}
}
