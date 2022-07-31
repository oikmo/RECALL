package engineTester;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import models.RawModel;
import models.TexturedModel;
import objConverter.ModelData;
import objConverter.OBJFileLoader;

import org.lwjgl.opengl.Display;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

import renderEngine.DisplayManager;
import renderEngine.Loader;
import renderEngine.MasterRenderer;
import terrain.Terrain;
import textures.ModelTexture;
import entities.Camera;
import entities.Entity;
import entities.Light;
import entities.Player;
import gui.GuiRenderer;
import gui.GuiTexture;

public class MainGameLoop {

	public static void main(String[] args) {
		//RENDERERS
		
		DisplayManager.createDisplay();
		Loader loader = new Loader();
		MasterRenderer renderer = new MasterRenderer();
		
		//ATLAS
		ModelTexture grassTextureAtlas = new ModelTexture(loader.loadTexture("grassAtlas"));
		grassTextureAtlas.setNumberOfRows(2);
		ModelTexture treeTex = new ModelTexture(loader.loadTexture("tree"));
		treeTex.setNumberOfRows(1);
		//TERRAIN
		ModelData dataGrass = OBJFileLoader.loadOBJ("grassModel");
		RawModel rawGrass = loader.loadToVAO(dataGrass.getVertices(), dataGrass.getTextureCoords(), dataGrass.getNormals(), dataGrass.getIndices());
		TexturedModel grassTexturedModel = new TexturedModel(rawGrass, grassTextureAtlas);
		grassTexturedModel.getTexture().setHasTransparency(true);
		grassTexturedModel.getTexture().setUseFakeLighting(true);
		
		ModelData dataTree = OBJFileLoader.loadOBJ("tree");
		RawModel rawTree = loader.loadToVAO(dataTree.getVertices(), dataTree.getTextureCoords(), dataTree.getNormals(), dataTree.getIndices());
		TexturedModel treeTexturedModel = new TexturedModel(rawTree, treeTex);
		
		ModelTexture green = new ModelTexture(loader.loadTexture("greenBrick"));
		green.setNumberOfRows(1);
		Terrain terrain = new Terrain(0,-1,loader, green, "heightMap");
		//Terrain terrain2 = new Terrain(-1,-1,loader, green, "heightMap");
		
		//GENERATE TERRAIN E.G GRASS AND TREE
		List<Entity> entities = new ArrayList<Entity>();
		Random random = new Random();
		for(int i=0;i<500;i++){
			float x = random.nextFloat() * 800 - 400;
			float z = random.nextFloat() * -600;
			float y = terrain.getHeightOfTerrain(x,z);
			entities.add(new Entity(treeTexturedModel, new Vector3f(x, y, z),0,0,0,3));
		}
		for(int i=0;i<1000;i++) {
			float x = random.nextFloat() *1200 - 600;
			float z = random.nextFloat() * -600;
			float y = terrain.getHeightOfTerrain(x,z);
			entities.add(new Entity(grassTexturedModel, random.nextInt(4),new Vector3f(x,y,z),0,0,0,1));
		}
		
		Light sun = new Light(new Vector3f(0,1000,-7000),new Vector3f(0.5f,0.5f,0.5f));
		//Light lamp = new Light(new Vector3f(185,10,-293),new Vector3f(2,0,0), new Vector3f(1, 0.01f, 0.002f));
		List<Light> lights = new ArrayList<Light>();
		lights.add(sun);
		//lights.add(lamp);
		
		//List<GuiTexture> guis = new ArrayList<GuiTexture>();
		//GuiTexture gui1 = new GuiTexture(loader.loadTexture("jim"), new Vector2f(0.5f, 0.5f), new Vector2f(0.25f, 0.25f));
		//guis.add(gui1);
		
		//PLAYER
		ModelData dataPlayer = OBJFileLoader.loadOBJ("player"); 
		RawModel rawPlayer = loader.loadToVAO(dataPlayer.getVertices(), dataPlayer.getTextureCoords(), dataPlayer.getNormals(), dataPlayer.getIndices());
		TexturedModel playerTexturedModel = new TexturedModel(rawPlayer, new ModelTexture(loader.loadTexture("player")));
		
		Player player = new Player(playerTexturedModel, new Vector3f(00, 0, 00), 0, 0, 0, 0.5f);
		Camera camera = new Camera(player);
		GuiRenderer guiRenderer = new GuiRenderer(loader);
		
		
		//MAIN GAME LOOP
		while(!Display.isCloseRequested()){
			camera.move();
			player.move(terrain);
			renderer.processEntity(player);
			renderer.processTerrain(terrain);
			//renderer.processTerrain(terrain2);
			
			for(Entity entity:entities){
				renderer.processEntity(entity);
			}
			renderer.render(lights, camera);
			//guiRenderer.render(guis);
			DisplayManager.updateDisplay();
		}
		guiRenderer.cleanUp();
		renderer.cleanUp();
		loader.cleanUp();
		DisplayManager.closeDisplay();

	}

}