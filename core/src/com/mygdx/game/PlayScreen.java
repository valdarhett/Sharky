package com.mygdx.game;

import com.badlogic.gdx.Application.ApplicationType;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.actions.AfterAction;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public class PlayScreen implements Screen, InputProcessor
{
    OrthographicCamera gamecam;
    Viewport gameport;
    Texture img;
    World w;
    MainPlayer mPlayer;
    Box2DDebugRenderer b2dr;
    MyGdxGame game;

    private float speed = 100;

    private TmxMapLoader mapLoader;
    private TiledMap map;
    private OrthogonalTiledMapRenderer mapRenderer;

    private float stateTime;
    private TextureRegion currentFrame;

    private Animation sharkAnimation;
    private TextureRegion[][] sharkTmp;
    private TextureRegion[] sharkAnimationFrames;

    private Animation bgAnimation;
    private TextureRegion[][] bgTmp;
    private TextureRegion[] bgAnimationFrames;

    private Animation goldfishAnimation;
    private TextureRegion[][] goldfishTmp;
    private TextureRegion[] goldfishAnimationFrames;

    private Animation anglerfishAnimation;
    private TextureRegion[][] anglerfishTmp;
    private TextureRegion[] anglerfishAnimationFrames;

    private Angler angler = new Angler();

    private ShapeRenderer shapeRenderer;

    Array<Food> food2 = new Array<Food>();

    Controller controller;

    public PlayScreen(MyGdxGame game) {
	this.game = game;

	shapeRenderer = new ShapeRenderer();

	sharkTmp = game.atlas.findRegion("s_plyr_swim_strip10").split(
		game.atlas.findRegion("s_plyr_swim_strip10").getRegionWidth() / 10,
		game.atlas.findRegion("s_plyr_swim_strip10").getRegionHeight());
	sharkAnimationFrames = new TextureRegion[10];

	int index = 0;
	for (int i = 0; i < sharkTmp.length; i++)
	{
	    for (int j = 0; j < sharkTmp[0].length; j++)
	    {
		sharkAnimationFrames[index++] = sharkTmp[i][j];
	    }
	}
	sharkAnimation = new Animation(0.05f, sharkAnimationFrames);

	bgTmp = new TextureRegion(new Texture("graphics/unwater_s_sheet.png")).split(384, 640);
	bgAnimationFrames = new TextureRegion[75];

	index = 0;
	for (int i = 0; i < bgTmp.length; i++)
	{
	    for (int j = 0; j < bgTmp[0].length; j++)
	    {
		bgAnimationFrames[index++] = bgTmp[i][j];
	    }
	}
	bgAnimation = new Animation(0.05f, bgAnimationFrames);

	goldfishTmp = game.atlas.findRegion("goldfishspritesheet").split(
		game.atlas.findRegion("goldfishspritesheet").getRegionWidth() / 3,
		game.atlas.findRegion("goldfishspritesheet").getRegionHeight() / 5);
	goldfishAnimationFrames = new TextureRegion[15];

	index = 0;
	for (int i = 0; i < goldfishTmp.length; i++)
	{
	    for (int j = 0; j < goldfishTmp[0].length; j++)
	    {
		goldfishAnimationFrames[index++] = goldfishTmp[i][j];
	    }
	}
	goldfishAnimation = new Animation(0.05f, goldfishAnimationFrames);

	anglerfishTmp = game.atlas.findRegion("anglerfish_spritesheet").split(
		game.atlas.findRegion("anglerfish_spritesheet").getRegionWidth() / 4,
		game.atlas.findRegion("anglerfish_spritesheet").getRegionHeight() / 5);
	anglerfishAnimationFrames = new TextureRegion[20];

	index = 0;
	for (int i = 0; i < anglerfishTmp.length; i++)
	{
	    for (int j = 0; j < anglerfishTmp[0].length; j++)
	    {
		anglerfishAnimationFrames[index++] = anglerfishTmp[i][j];
	    }
	}
	anglerfishAnimation = new Animation(0.5f, anglerfishAnimationFrames);

	img = new Texture("badlogic.jpg");
	gamecam = new OrthographicCamera(MyGdxGame.WIDTH * MyGdxGame.PPM, MyGdxGame.HEIGHT * MyGdxGame.PPM);
	gameport = new FitViewport(MyGdxGame.WIDTH * MyGdxGame.PPM, MyGdxGame.HEIGHT * MyGdxGame.PPM, gamecam);
	gamecam.position.set((MyGdxGame.WIDTH / 2f) * MyGdxGame.PPM, (MyGdxGame.HEIGHT / 2f) * MyGdxGame.PPM, 0f);
	w = new World(new Vector2(0, 0f), true);
	mapLoader = new TmxMapLoader();
	map = mapLoader.load("Levels/map.tmx");
	mapRenderer = new OrthogonalTiledMapRenderer(map, MyGdxGame.PPM);

	new MapHandler(w, map);
	mPlayer = new MainPlayer(w);
	b2dr = new Box2DDebugRenderer();

	controller = new Controller(game);

	InputMultiplexer iMultiplexer = new InputMultiplexer();
	iMultiplexer.addProcessor(this);
	iMultiplexer.addProcessor(controller.stage);

	Gdx.input.setInputProcessor(iMultiplexer);
    }

    @Override
    public void show()
    {
    }

    @Override
    public void render(float delta)
    {
	Gdx.gl.glClearColor(0, 0, 0, 1);
	Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

	updateMethod();

	genFood();

	stateTime += delta;
	game.batch.setProjectionMatrix(gamecam.combined);
	game.batch.begin();

	// background
	currentFrame = bgAnimation.getKeyFrame(stateTime, true);
	game.batch.draw(currentFrame, gamecam.position.x - (currentFrame.getRegionWidth() * MyGdxGame.PPM / 2f),
		gamecam.position.y - (currentFrame.getRegionHeight() * MyGdxGame.PPM / 2f), 0, 0, 1, 1,
		384f * MyGdxGame.PPM, 640f * MyGdxGame.PPM, 0);

	// shark
	currentFrame = sharkAnimation.getKeyFrame(stateTime, true);
	game.batch.draw(currentFrame,
		(mPlayer.b2Body.getLinearVelocity().x < 0) ? mPlayer.b2Body.getPosition().x + 1f
			: mPlayer.b2Body.getPosition().x - 1f,
		mPlayer.b2Body.getPosition().y - 1f, 0, 0, (mPlayer.b2Body.getLinearVelocity().x < 0) ? -1 : 1, 1,
		32f * MyGdxGame.PPM * 2f, 32f * MyGdxGame.PPM * 2f, 0);

	// goldfish
	currentFrame = goldfishAnimation.getKeyFrame(stateTime, true);
	game.batch.draw(currentFrame, 3, 6, 0, 0, 1, -1, 32f * MyGdxGame.PPM * 2f, -32f * MyGdxGame.PPM * 2f, 0);

	// anglerfish
	currentFrame = anglerfishAnimation.getKeyFrame(stateTime, true);
	game.batch.draw(currentFrame, angler.getPos().x, angler.getPos().y, 0, 0, 1, -1, MyGdxGame.PPM * 64f,
		MyGdxGame.PPM * -64f, 0);
	game.batch.end();

	game.batch.setProjectionMatrix(controller.stage.getCamera().combined);
	if (Gdx.app.getType() == ApplicationType.Android)
	{
	    controller.draw();
	}
	mapRenderer.render();

	b2dr.render(this.w, gamecam.combined);

	// for (int i = 0; i < food.size; i++)
	// {
	//
	// shapeRenderer.setProjectionMatrix(gamecam.combined);
	// shapeRenderer.begin(ShapeType.Line);
	// shapeRenderer.circle(food.get(i).x, food.get(i).y, 0.5f);
	// shapeRenderer.end();
	// }
    }

    private void genFood()
    {
	if (food2.size < map.getLayers().get(2).getObjects().getCount())
	{
	    for (MapObject object : map.getLayers().get(2).getObjects().getByType(RectangleMapObject.class))
	    {
		Rectangle rect = ((RectangleMapObject) object).getRectangle();

		float x = rect.getX() * MyGdxGame.PPM
			+ (rect.getWidth() * 0.8f * MyGdxGame.PPM) * (float) Math.random();
		float y = rect.getY() * MyGdxGame.PPM
			+ (rect.getHeight() * 0.8f * MyGdxGame.PPM) * (float) Math.random();

		System.out.println(rect.getWidth() + " " + rect.getHeight());
		System.out.println(mPlayer.b2Body.getPosition().toString());

		food2.add(new Food(w, x, y));
	    }
	}
    }

    private void updateMethod()
    {

	if (Gdx.input.isKeyPressed(Keys.A))
	{
	    mPlayer.b2Body.applyForce(new Vector2(-speed * 10, 0), mPlayer.b2Body.getWorldCenter(), true);
	}
	if (Gdx.input.isKeyPressed(Keys.D))
	{
	    mPlayer.b2Body.applyForce(new Vector2(speed * 10, 0), mPlayer.b2Body.getWorldCenter(), true);
	}
	if (Gdx.input.isKeyPressed(Keys.W))
	{
	    mPlayer.b2Body.applyForce(new Vector2(0, 3 * 10), mPlayer.b2Body.getWorldCenter(), true);
	}
	if (Gdx.input.isKeyPressed(Keys.S))
	{
	    mPlayer.b2Body.applyForce(new Vector2(0, -3 * 10), mPlayer.b2Body.getWorldCenter(), true);
	}

	if (controller.downPressed)
	{
	    mPlayer.b2Body.applyForce(new Vector2(0, -3 * 10), mPlayer.b2Body.getWorldCenter(), true);
	}
	if (controller.upPressed)
	{
	    mPlayer.b2Body.applyForce(new Vector2(0, 3 * 10), mPlayer.b2Body.getWorldCenter(), true);
	}
	if (controller.leftPressed)
	{
	    mPlayer.b2Body.applyForce(new Vector2(-speed * 10, 0), mPlayer.b2Body.getWorldCenter(), true);
	}
	if (controller.rightPressed)
	{
	    mPlayer.b2Body.applyForce(new Vector2(speed * 10, 0), mPlayer.b2Body.getWorldCenter(), true);
	}

	float xv = mPlayer.b2Body.getLinearVelocity().x;
	float yv = mPlayer.b2Body.getLinearVelocity().y;
	xv = MathUtils.clamp(xv, -5, 5);
	yv = MathUtils.clamp(yv, -5, 5);
	mPlayer.b2Body.setLinearVelocity(xv, yv);
	w.step(1 / 60f, 6, 2);

	float x = MathUtils.clamp(mPlayer.b2Body.getPosition().x, 0, 200);
	float y = MathUtils.clamp(mPlayer.b2Body.getPosition().y, 0, 19);

	mPlayer.b2Body.setTransform(x, y, 0);

	gamecam.position.x = mPlayer.b2Body.getPosition().x;

	gamecam.update();
	mapRenderer.setView(gamecam);
	mPlayer.Update();

	if (angler.getPos().x < 15)
	{
	    angler.setPos(new Vector2(angler.getPos().x + 0.1f, angler.getPos().y));
	} else
	{
	    angler.setPos(new Vector2(angler.getPos().x - 0.1f, angler.getPos().y));
	}
    }

    @Override
    public void resize(int width, int height)
    {
	gamecam.position.x = mPlayer.b2Body.getPosition().x;
	gameport.update(width, height);
	game.batch.setProjectionMatrix(gameport.getCamera().combined);
	controller.resize(width, height);
    }

    @Override
    public void pause()
    {
    }

    @Override
    public void resume()
    {
    }

    @Override
    public void hide()
    {
    }

    @Override
    public void dispose()
    {
    }

    @Override
    public boolean keyDown(int keycode)
    {

	return true;
    }

    @Override
    public boolean keyUp(int keycode)
    {
	return true;
    }

    @Override
    public boolean keyTyped(char character)
    {
	return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button)
    {
	System.out.println("touchdown");
	return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button)
    {
	return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer)
    {
	return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY)
    {
	return false;
    }

    @Override
    public boolean scrolled(int amount)
    {
	return false;
    }

    public class Angler
    {
	private Vector2 pos;

	public Vector2 getPos()
	{
	    return pos;
	}

	public void setPos(Vector2 pos)
	{
	    this.pos = pos;
	}

	public Angler() {
	    pos = new Vector2(10, 6);
	}

    }
}
