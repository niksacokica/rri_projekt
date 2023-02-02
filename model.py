import tensorflow as tf
import matplotlib.pyplot as plt
import numpy as np

model = tf.keras.models.load_model("model")

def get_spectrogram(waveform):
	spectrogram = tf.signal.stft(waveform, frame_length=255, frame_step=128)
	spectrogram = tf.abs(spectrogram)
	spectrogram = spectrogram[..., tf.newaxis]
	return spectrogram

x = tf.io.read_file(str("sound.wav"))
x, sample_rate = tf.audio.decode_wav(x, desired_channels=1, desired_samples=16000,)
x = tf.squeeze(x, axis=-1)
x = get_spectrogram(x)
x = x[tf.newaxis,...]

prediction = model(x)
labels = [
	'brezje',
	'center',
	'cesta',
	'dravograjska',
	'gosposvetska',
	'koroška',
	'krekova',
	'križišče',
	'ljubljanska',
	'maribor',
	'pobreška',
	'pohorska',
	'ptujska',
	'razlagova',
	'rondo',
	'rutar',
	'selo',
	'smetanova',
	'tabor',
	'tezno',
	'tržaška',
	'turnerjeva',
	'ulica',
	'vrbanska',
	'zagrebška'
]
print(labels[np.argmax(prediction[0].numpy())])