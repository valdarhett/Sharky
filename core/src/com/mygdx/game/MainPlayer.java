
package com.mygdx.game;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;

/**
 * Created by my on 8/17/2016.
 */
public class MainPlayer extends Sprite
{

    public World world;
    public Body b2Body;
    private Circle boundary;
    private float circleRadius = 32f * MyGdxGame.PPM;

    public MainPlayer(World world) {
	this.world = world;
	defineMainPlayer();
    }

    public void defineMainPlayer()
    {
	BodyDef bdef = new BodyDef();
	bdef.position.set(32f * MyGdxGame.PPM,1.5f* 64F * MyGdxGame.PPM);
	bdef.type = BodyDef.BodyType.DynamicBody;

	b2Body = world.createBody(bdef);

	FixtureDef fdef = new FixtureDef();
	CircleShape shape = new CircleShape();
	fdef.restitution = 0.05f;
	fdef.density = 1f;

	setBoundary(new Circle(1f, 1f, circleRadius));

	shape.setRadius(circleRadius);

	fdef.shape = shape;
	b2Body.createFixture(fdef);
    }

    public void Update()
    {
	this.boundary.setPosition(this.b2Body.getPosition().x, this.b2Body.getPosition().y);
    }

    public Circle getBoundary()
    {
	return boundary;
    }

    public void setBoundary(Circle boundary)
    {
	this.boundary = boundary;
    }

}