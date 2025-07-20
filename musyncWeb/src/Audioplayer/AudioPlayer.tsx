import { useRef } from "react"

const AudioPlayer = () => {
    const audioRef = useRef<HTMLAudioElement | null>(null);

    const handlePlay = () => {
        if (audioRef.current) {
            audioRef.current.currentTime = 0
            
            audioRef.current.play().catch((err) => {
                console.log("❌ Play Failed: ", err)
            });
        }
    }

  return (
    <div className="w-full max-w-md mx-auto mt-10 px-4 space-y-4">
        <h2 className="text-xl text-white text-center">Static Audio 🎶</h2>

        <audio
            preload="auto"
            ref={audioRef} 
            controls
            src="/track.mp3"
            className="w-full rounded-lg shadow-md"
            >
                Your browser does not support audio element
        </audio>
        <button
            onClick={handlePlay}
            className="w-full py-2 bg-green-400 hover:bg-green-600 rounded text-white"
        >
            ▶️ Play Audio
        </button>
    </div>
  )
}

export default AudioPlayer