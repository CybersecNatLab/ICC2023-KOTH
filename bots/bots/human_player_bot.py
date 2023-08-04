
import pygame
from pygame.locals import *
import numpy as np
import struct
import json
import queue
file= 'tmw_desert_spacing.png'


# maps tiles to tiles in file above
tile_map={0:37, 1:45,2:7,3:46,4:10,5:41,6:47}

player_tile=pygame.Surface((32,32))
player_tile.blit(pygame.image.load("./bots/player.png"), (0, 0), (0, 0, *(32,32)))

class Tileset:
    def __init__(self, file, size=(32, 32), margin=1, spacing=1):
        self.file = file
        self.size = size
        self.margin = margin
        self.spacing = spacing
        self.image = pygame.image.load(file)
        self.rect = self.image.get_rect()
        self.tiles = []
        self.load()

    def load(self):

        self.tiles = []
        x0 = y0 = self.margin
        w, h = self.rect.size
        dx = self.size[0] + self.spacing
        dy = self.size[1] + self.spacing
        
        for x in range(x0, w, dx):
            for y in range(y0, h, dy):
                tile = pygame.Surface(self.size)
                tile.blit(self.image, (0, 0), (x, y, *self.size))
                self.tiles.append(tile)

    def __str__(self):
        return f'{self.__class__.__name__} file:{self.file} tile:{self.size}'


class Bot():
    W = 640
    H = 240
    SIZE = W, H
    move_queue = queue.Queue()

    def __init__(self, tileset, size=(10, 20), rect=None):
        self.size = size
        self.tileset = tileset

        h, w = self.size
        self.image = pygame.Surface((32*w, 32*h))
        if rect:
            self.rect = pygame.Rect(rect)
        else:
            self.rect = self.image.get_rect()
        pygame.init()
        self.screen = pygame.display.set_mode((32*w/2, 32*h/2))
        pygame.display.set_caption("Pygame Tiled Demo")
        self.running = True
        
    def run(self):
        while self.running:
            for event in pygame.event.get():
                if event.type == QUIT:
                    self.running = False
                if event.type == KEYDOWN:
                    if(event.key == pygame.K_e):
                        self.move_queue.put("e")
                    if(event.key == pygame.K_w):
                        self.move_queue.put("w")
                    if(event.key == pygame.K_s):
                        self.move_queue.put("s")
                    if(event.key == pygame.K_a):
                        self.move_queue.put("a")
                    if(event.key == pygame.K_d):
                        self.move_queue.put("d")
                    if(event.key == pygame.K_p):
                        self.move_queue.put("p")
                    if(event.key == pygame.K_h):
                        self.move_queue.put("h")
                    if(event.key == pygame.K_1):
                        self.move_queue.put("1")
                    if(event.key == pygame.K_2):
                        self.move_queue.put("2")
                    if(event.key == pygame.K_3):
                        self.move_queue.put("3")
                    if(event.key == pygame.K_4):
                        self.move_queue.put("4")
                    if(event.key == pygame.K_5):
                        self.move_queue.put("5")
                    if(event.key == pygame.K_6):
                        self.move_queue.put("6")
                    if(event.key == pygame.K_7):
                        self.move_queue.put("7")
                    if(event.key == pygame.K_8):
                        self.move_queue.put("8")
                    
        
        pygame.quit()
                    

    def render(self, map):
        m, n = map["width"], map["height"]
        for i in range(m):
            for j in range(n):
                bg=tile_map[map["map"][i][j]["background"]]
                tile = self.tileset.tiles[bg]
                self.image.blit(tile, (i*32, j*32))

        for i in range(m):
            for j in range(n):
                if("entity" in map["map"][i][j]):
                    # if (map["map"][i][j]["entity"]["type"]=="summoner"):
                        self.image.blit(player_tile, (i*32, j*32))
        surface_mod = pygame.transform.rotozoom(self.image, 0, 0.5)
        self.screen.blit(surface_mod, surface_mod.get_rect())
        pygame.display.update()
    