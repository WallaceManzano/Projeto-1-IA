package utils;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Comparator;
import java.util.RandomAccess;

/**
 * Fila de prioridade generica implementada por uma MinHeap em um vetor, e para
 * isso tendo seus elementos sendo comparados por um
 * {@linkplain ComparatorDouble compadador} que deve ser passado pelo
 * construtor.
 * <p>
 * E tendo seu tamanho aumentado dinâmicamente caso necessario.
 * </p>
 * 
 * @author Wallace Alves Esteves Manzano
 *
 * @param <T>
 *            Tipo dos elementos que serão armazenados na fila.
 * 
 * @see Comparator
 * @see RandomAccess
 * @see Serializable
 */
public class PriorityQueue<T> implements java.io.Serializable {
	private static final long serialVersionUID = -697219392813040146L;
	private static final int DEFAULT_INITIAL_CAPACITY = 10;
	private Object[] fila;
	private int size;
	private ComparatorDouble<? super T> comparator;

	/**
	 * Cria uma {@code FilaPrioridade} com a capacidade inicial estipulada
	 * 
	 * @param comparator
	 *            {@linkplain ComparatorDouble comparador} que fará a ordenação da
	 *            fila de prioridade, por meio de uma MinHeap.
	 * @param capacidadeInicial
	 *            capacidade inicial da fila.
	 * @throws IllegalArgumentException
	 *             caso capacidade inicial seja passada menor que 1.
	 */
	public PriorityQueue(ComparatorDouble<? super T> comparator, int capacidadeInicial) {
		if (capacidadeInicial < 1)
			throw new IllegalArgumentException();
		this.comparator = comparator;
		this.fila = new Object[capacidadeInicial];
	}

	/**
	 * Cria uma {@code FilaPrioridade} com a capacidade inicial padrão.
	 * 
	 * @param comparator
	 *            {@linkplain ComparatorDouble comparador} que fará a ordenação da
	 *            fila de prioridade, por meio de uma MinHeap
	 */
	public PriorityQueue(ComparatorDouble<? super T> comparator) {
		this(comparator, DEFAULT_INITIAL_CAPACITY);
	}

	public int size() {
		return size;
	}

	/**
	 * Verifica se a fila esta vazia.
	 * 
	 * @return true se a fila estiver vazia.
	 */
	public boolean isEmpty() {
		return size == 0;
	}

	/**
	 * Verifica se um elemento esta na fila.
	 * 
	 * @param o
	 *            elemento a ser verificado.
	 * @return true se o elemento esta na fila.
	 */
	public boolean contains(T o) {
		for (int i = 0; i < size; i++)
			if (fila[i].equals(o))
				return true;
		return false;
	}

	public Object[] toArray() {
		return Arrays.copyOf(fila, size);
	}

	/**
	 * Remove todos os elementos da fila.
	 */
	public void clear() {
		for (int i = 0; i < size; i++)
			fila[i] = null;
		size = 0;

	}

	/**
	 * Adiciona um elemento a fila de prioridade.
	 * 
	 * @param e
	 *            elemento a ser adicionado.
	 * @return true.
	 */
	public boolean add(T e) {
		return offer(e);
	}

	/**
	 * Adiciona um elemento a fila de prioridade.
	 * 
	 * @param e
	 *            elemento a ser adicionado.
	 * @return true.
	 */
	public boolean offer(T e) {
		int aux = size;
		size++;
		if (aux == fila.length) {
			grow();
		}
		if (aux == 0) {
			fila[0] = e;
		} else
			bubbleUp(aux, e);
		return true;
	}

	/**
	 * Retira e retorna o elemento de maior prioridade na fila.
	 * 
	 * @return O elemento de maior prioridade da fila.
	 */
	@SuppressWarnings("unchecked")
	public T poll() {
		if (size == 0)
			return null;
		int s = --size;
		if (s <= (fila.length >>> 2))
			shrink();

		T result = (T) fila[0];
		T x = (T) fila[s];
		fila[s] = null;
		if (s != 0)
			bubbleDown(0, x);
		return result;
	}

	/**
	 * Retorna, sem remover, o elemento de maior prioridade da fila.
	 * 
	 * @return O elemento de maior prioridade.
	 */
	@SuppressWarnings("unchecked")
	public T peek() {
		return (size == 0) ? null : (T) fila[0];
	}

	private void grow() {
		int oldCapacity = fila.length;
		int newCapacity = oldCapacity + ((oldCapacity < 64) ? (oldCapacity + 2) : (oldCapacity >> 1));
		fila = Arrays.copyOf(fila, newCapacity);
	}

	private void shrink() {
		int oldCapacity = fila.length;
		if (oldCapacity > 64) {
			int newCapacity = oldCapacity >> 1;
			fila = Arrays.copyOf(fila, newCapacity);
		}
	}

	@SuppressWarnings("unchecked")
	private void bubbleUp(int a, T e) {
		while (a > 0) {
			int pai = (a - 1) >>> 1;
			Object o = fila[pai];
			if (comparator.compare(e, (T) o) >= 0)
				break;
			fila[a] = o;
			a = pai;
		}
		fila[a] = e;
	}

	@SuppressWarnings("unchecked")
	private void bubbleDown(int a, T e) {
		while (a < (size >>> 1)) {
			int filho = (a << 1) + 1;
			Object c = fila[filho];
			int filhoD = filho + 1;
			if (filhoD < size && comparator.compare((T) c, (T) fila[filhoD]) > 0)
				c = fila[filho = filhoD];
			if (comparator.compare(e, (T) c) <= 0)
				break;
			fila[a] = c;
			a = filho;
		}
		fila[a] = e;
	}

}
