package com.mygdx.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.ScreenUtils;

import java.util.Random;

public class FlappyBird1301 extends ApplicationAdapter {
	SpriteBatch batch;
	Texture background;
	Texture [] bird;
	float birdY = 0;
	float volocity = 0;

	int flapState = 0;
	int gameState;
	float gravity = 2;
	Texture topTube, bottomTube;
	float gap = 400;

	//Create a random tubes structure
	float maxTubeOffset = 0;
	Random randomGenerator;
	float [] tubeX = new float[40];
	float [] tubeOffset = new float[40];
	float distanceBetweenTubes;
	//Moving the background
	Texture background2; // second instance of the background
	float backgroundX = 0; // x position of the first background
	float backgroundX2; // x position of the second background
	float backgroundSpeed = 3; // speed of the background movement
	double tubeSpeed = 3.5; // speed of the tube movement
	//Create a shape for the bird and the tubes
	Rectangle birdRectangle;
	Rectangle[] topTubeRectangles;
	Rectangle[] bottomTubeRectangles;
	int score = 0;
	int recentScore = 0;
	int scoringTube = 0;
	//Score features
	BitmapFont fontScore, fontOver;
	GlyphLayout layout;
	//Play button
	Texture playButton;
	//Sound
	Sound wingSound, dieSound, hitSound, pointSound;



	@Override
	public void create () {
		//Sound
		wingSound = Gdx.audio.newSound(Gdx.files.internal("wing.wav"));
		dieSound = Gdx.audio.newSound(Gdx.files.internal("die.wav"));
		hitSound = Gdx.audio.newSound(Gdx.files.internal("hit.wav"));
		pointSound = Gdx.audio.newSound(Gdx.files.internal("point.wav"));

		//Draw
		batch = new SpriteBatch();
		background = new Texture("bg.png");
		topTube = new Texture("toptube.png");
		bottomTube = new Texture("bottomtube.png");
		playButton = new Texture("playbtn.png");


		bird = new Texture[2];
		bird [0] = new Texture("bird.png");
		bird [1] = new Texture("bird2.png");
		birdY = Gdx.graphics.getHeight() / 2 - bird[0].getHeight() / 2;



		//Create a random tubes structure
		randomGenerator = new Random();
		distanceBetweenTubes = Gdx.graphics.getWidth() ;
		maxTubeOffset = Gdx.graphics.getHeight() / 2 - gap / 2 - 100;

		//Moving the background
		background2 = new Texture("bg.png");
		backgroundX2 = background.getWidth();

		//Create a shape for the bird and the tubes
		birdRectangle = new Rectangle();
		topTubeRectangles = new Rectangle[40];
		bottomTubeRectangles = new Rectangle[40];
		for(int i = 0; i < 40; i++) {
			topTubeRectangles[i] = new Rectangle();
			bottomTubeRectangles[i] = new Rectangle();
		}

		for (int i = 0; i < 40; i++) {
			tubeOffset[i] = (randomGenerator.nextFloat() - 0.5f) * (Gdx.graphics.getHeight() - gap - 200);

			// set the initial position of the tubes
			tubeX[i] = Gdx.graphics.getWidth() + i * distanceBetweenTubes;
		}

		//Score features
		fontScore = new BitmapFont();
		fontScore.getData().setScale(10);
		layout = new GlyphLayout();

		//Font game over
		fontOver = new BitmapFont();
		fontOver.getData().setScale(6);

		gameState = 0;
		resetGame();
	}

	@Override
	public void render () {
		ScreenUtils.clear(1, 0, 0, 1);
		batch.begin();
		batch.draw(background, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		batch.draw(background2, backgroundX2, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

		//Moving the background
		backgroundX -= backgroundSpeed;

		//Touch event
		if(gameState != 0 && gameState != 2) {
			batch.draw(bird[flapState], Gdx.graphics.getWidth() / 2 , birdY );

			//Draw score
			layout.setText(fontScore, "" + score);
			fontScore.draw(batch, layout, Gdx.graphics.getWidth() / 2 - layout.width / 2, Gdx.graphics.getHeight() - layout.height - 20);

			// if the user touches the screen, make the bird jump
			if(Gdx.input.justTouched()) {
				volocity = -24;
				flapState = 1;
				wingSound.play();
//				batch.draw(bird[2], Gdx.graphics.getWidth() / 2 , birdY );
			}
			if(volocity > 0) {
				flapState = 0;
			}


//			batch.draw(topTube, Gdx.graphics.getWidth() / 2 - topTube.getWidth() / 2, Gdx.graphics.getHeight() / 2 + gap / 2);
//			batch.draw(bottomTube, Gdx.graphics.getWidth() / 2 - bottomTube.getWidth() / 2, Gdx.graphics.getHeight() / 2 - gap / 2 - bottomTube.getHeight());
			for (int i = 0; i < 20; i++) {
				// move the tubes
				tubeX[i] -= tubeSpeed;

				// if a tube is off the screen, reset its position
//				if (tubeX[i] + topTube.getWidth() <= 0) {
//					tubeX[i] = Gdx.graphics.getWidth();
//				}

				batch.draw(topTube, tubeX[i], Gdx.graphics.getHeight() / 2 + gap / 2 + tubeOffset[i]);
				batch.draw(bottomTube, tubeX[i], Gdx.graphics.getHeight() / 2 - gap / 2 - bottomTube.getHeight() + tubeOffset[i]);
			}

			if(birdY > 0 || volocity < 0) {
				volocity = volocity + gravity;
				birdY -= volocity;
			}

			//Gameplay
			birdRectangle.set(Gdx.graphics.getWidth() / 2, birdY, bird[flapState].getWidth(), bird[flapState].getHeight());
			for(int i = 0; i < 40; i++) {
				topTubeRectangles[i].set(tubeX[i], Gdx.graphics.getHeight() / 2 + gap / 2 + tubeOffset[i], topTube.getWidth(), topTube.getHeight());
				bottomTubeRectangles[i].set(tubeX[i], Gdx.graphics.getHeight() / 2 - gap / 2 - bottomTube.getHeight() + tubeOffset[i], bottomTube.getWidth(), bottomTube.getHeight());

				//Game over
				if(birdRectangle.overlaps(topTubeRectangles[i]) || birdRectangle.overlaps(bottomTubeRectangles[i])) {
					resetGame();
					gameState = 2;
					hitSound.play();
				} else if (birdY <= 0) {
					resetGame();
					gameState = 2;
					dieSound.play();

				}
				//Game score
				if (tubeX[i] < Gdx.graphics.getWidth() / 2 && tubeX[i] + topTube.getWidth() > Gdx.graphics.getWidth() / 2 && i == scoringTube) {
					score++;
					pointSound.play();
					if (scoringTube < 39) {
						scoringTube++;
					} else {
						scoringTube = 2;
					}
				}
			}
		}else{
			if(Gdx.input.justTouched()) {
				gameState = 1;
			}
		}

		//Game over
		if(gameState == 2 || birdY <= 0) {
			layout.setText(fontOver, "Game Over");
			fontOver.draw(batch, layout, Gdx.graphics.getWidth() / 2 - layout.width /2 , Gdx.graphics.getHeight() * 2 / 3);
			layout.setText(fontScore, "Score: " + score);
			fontScore.draw(batch, layout, Gdx.graphics.getWidth() / 2 - layout.width / 2, Gdx.graphics.getHeight() * 1 / 2);
			layout.setText(fontOver, "Tap to play again");
			fontOver.draw(batch, layout, Gdx.graphics.getWidth() / 2 - layout.width / 2, Gdx.graphics.getHeight() *1 / 3 );
		}

		//Start button
		if (gameState == 0) {
			// Draw the play button in the center of the screen
			float x = Gdx.graphics.getWidth() / 2 - playButton.getWidth() / 2;
			float y = Gdx.graphics.getHeight() / 2 - playButton.getHeight() / 2;
			batch.draw(playButton, x, y);

			// Check if the play button is touched
			if (Gdx.input.justTouched()) {
				float touchX = Gdx.input.getX();
				float touchY = Gdx.graphics.getHeight() - Gdx.input.getY(); // We subtract from the height because libGDX's y-axis is flipped
				Rectangle playButtonBounds = new Rectangle(x, y, playButton.getWidth(), playButton.getHeight());
				if (playButtonBounds.contains(touchX, touchY)) {
					gameState = 1;
				}
			}
		}

		batch.end();
	}

	@Override
	public void dispose () {
		batch.dispose();
		background.dispose();
		dieSound.dispose();
		hitSound.dispose();
		wingSound.dispose();
		pointSound.dispose();
	}
	private void resetGame() {
		birdY = Gdx.graphics.getHeight() / 2 - bird[0].getHeight() / 2;
		for (int i = 0; i < 40; i++) {
			tubeOffset[i] = (randomGenerator.nextFloat() - 0.5f) * (Gdx.graphics.getHeight() - gap - 200);
			tubeX[i] = Gdx.graphics.getWidth() + i * distanceBetweenTubes;
		}
		volocity = 0;
		score = 0;
		scoringTube = 0;
		gameState = 0;
	}
}
