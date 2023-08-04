import GameCanvas from './GameCanvas';
import { FC, useEffect, useRef, useState } from 'react';
import { Map, Update } from './map';

const ResizableGameCanvas: FC<{
  map: Map;
  updates: Update[];
  updateIndex: number;
  spritesSeed: number;
}> = ({ map, updates, updateIndex, spritesSeed }) => {
  const ref = useRef<HTMLDivElement>(null);
  const [canvasWidth, setCanvasWidth] = useState<number>(0);
  const [canvasHeight, setCanvasHeight] = useState<number>(0);

  useEffect(() => {
    if (!ref.current) {
      return;
    }

    setCanvasWidth(ref.current.clientWidth);
    setCanvasHeight(ref.current.clientHeight);

    const listener = () => {
      if (!ref.current) {
        return;
      }

      setCanvasWidth(ref.current.clientWidth);
      setCanvasHeight(ref.current.clientHeight);
    };

    window.addEventListener('resize', listener);

    return () => {
      window.removeEventListener('resize', listener);
    };
  }, []);

  return (
    <div
      ref={ref}
      style={{
        height: '100%',
        width: '100%',
        display: 'flex',
        flexDirection: 'row',
        alignItems: 'center',
        justifyContent: 'center'
      }}
    >
      <GameCanvas
        canvasWidth={canvasWidth}
        canvasHeight={canvasHeight}
        map={map}
        updates={updates}
        updateIndex={updateIndex}
        spritesSeed={spritesSeed}
      />
    </div>
  );
};

export default ResizableGameCanvas;
