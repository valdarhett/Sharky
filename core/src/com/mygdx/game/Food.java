
package com.mygdx.game;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
import com.sun.glass.ui.TouchInputSupport;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.ai.steer.behaviors.Flee;

/**
 * Created by my on 8/17/2016.
 */
public class Food extends Sprite
{

    public World world;
    public Body b2Body;
    private Circle boundary;
    private float circleRadius = 18f * MyGdxGame.PPM;
    private float x, y;

    public Food(World world, float x, float y) {
	this.world = world;
	definefood();
	this.x = x;
	this.y = y;
	definefood();
    }

    public void definefood()
    {
	BodyDef bdef = new BodyDef();
	bdef.position.set(x * 36f * MyGdxGame.PPM, y * 36f * MyGdxGame.PPM);
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

//	shape.setRadius(circleRadius * 6f);
//	fdef.shape = shape;
//	fdef.isSensor = true;

//	b2Body.createFixture(fdef);
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