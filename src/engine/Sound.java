package engine;

import java.net.URL;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;



public class Sound {
	
	Clip clip;
	Clip[] clips = new Clip[30];
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
		soundURL[8] = getClass().getResource("/sounds/jump_sound.wav");
		soundURL[9] = getClass().getResource("/sounds/fundo_esgoto_sound.wav");
		soundURL[10] = getClass().getResource("/sounds/walking_sewage_sound.wav");
		soundURL[11] = getClass().getResource("/sounds/water_toxic_sound.wav");
		soundURL[12] = getClass().getResource("/sounds/escadas_sound.wav");
		soundURL[13] = getClass().getResource("/sounds/fundo_fase_1_sound.wav");
		soundURL[14] = getClass().getResource("/sounds/fundo_fase_2_sound.wav");
		soundURL[15] = getClass().getResource("/sounds/fundo_fase_3_sound.wav");
		soundURL[16] = getClass().getResource("/sounds/fundo_fase_4_sound.wav");
		//soundURL[] = getClass().getResource("/sounds/escada_sound.wav");
		preLoad(0);
	}
	
	public void preLoad(int i) {
		try {
			if(clips[i] == null) {
				AudioInputStream ais = AudioSystem.getAudioInputStream(soundURL[i]);
				clips[i] = AudioSystem.getClip();
				clips[i].open(ais);
			}
		} catch (Exception e) {
			System.out.println("Erro ao pré-carregar: " + e.getMessage());
		}
	}
	
	public void playSE(int i) {
		try {
			if (clips[i] == null) preLoad(i);
			clips[i].stop();
			clips[i].setFramePosition(0);
			clips[i].start();
		} catch (Exception e) {
			System.out.println("Erro no SE: " + e.getMessage());
		}
	}
	
	public void setFile(int i) {
		try {
			
			if (clip != null) {
				if (clip.isOpen()) clip.close();
			}
			
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
	
	public void pause() {
		if (clip != null) {
		}
	}
}
